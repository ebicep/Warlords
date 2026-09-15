package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class EndlessParadoxOption extends AbstractAnomalyOption {

    private static final int COLLECTION_DURATION_TICKS = 180 * GameRunnable.SECOND;
    private static final int ALTAR_CHARGE_TICKS = 15 * GameRunnable.SECOND;
    private static final int BOSS_SPAWN_DELAY_TICKS = 3 * GameRunnable.SECOND;
    private static final int BASE_FRAGMENT_COUNT = 3;
    private static final int BASE_GUARDS_PER_FRAGMENT = 2;
    private static final int MAX_SCATTER_ATTEMPTS = 80;
    private static final int GROUND_SEARCH_RANGE = 16;
    private static final double ALTAR_RADIUS_SQUARED = 25;
    private static final double MIN_SCATTER_RADIUS = 18;
    private static final double MAX_SCATTER_RADIUS = 40;
    private static final double MIN_FRAGMENT_SEPARATION_SQUARED = 64;
    private static final double GUARD_SPAWN_MIN_DISTANCE = 4;
    private static final double GUARD_SPAWN_MAX_DISTANCE = 8;
    private static final Particle.DustOptions ALTAR_DUST = new Particle.DustOptions(Color.fromRGB(180, 80, 255), 1.4f);
    private static final Particle.DustOptions CHARGE_DUST = new Particle.DustOptions(Color.fromRGB(255, 210, 80), 1.6f);
    private static final ItemStack FRAGMENT_ITEM = new ItemBuilder(Material.AMETHYST_SHARD)
            .name(Component.text("Timeline Fragment", NamedTextColor.LIGHT_PURPLE))
            .lore(
                    Component.text("A shard of the Illusion Dynasty's broken timeline.", NamedTextColor.GRAY),
                    Component.text("Charge it at the altar for 15 seconds.", NamedTextColor.AQUA),
                    Component.text("Dying shatters this fragment.", NamedTextColor.RED)
            )
            .glow()
            .get();

    private final boolean[] cacheEligibility = new boolean[3];
    private final List<TimelineFragment> fragments = new ArrayList<>();
    private final Map<UUID, CarrierState> carriers = new HashMap<>();

    private Location altarLocation;
    private AbstractMob activeBoss;
    private SimpleScoreboardHandler objectiveScoreboardHandler;
    private int preparationTicks = START_DELAY_TICKS;
    private int collectionTicksRemaining;
    private int bossSpawnDelayTicks;
    private int fragmentsRequired;
    private int fragmentsDelivered;
    private boolean collectionStarted;
    private boolean bossPhase;
    private boolean failed;

    @Override
    public void register(@Nonnull Game game) {
        super.register(game);
        altarLocation = game.getMarkers(TimelineAltarMarker.class)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Endless Paradox requires a timeline altar marker"))
                .getLocation()
                .clone();

        game.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onFragmentPickup(EntityPickupItemEvent event) {
                TimelineFragment fragment = getFragmentByDrop(event.getItem());
                if (fragment == null) {
                    return;
                }
                event.setCancelled(true);
                if (!(event.getEntity() instanceof Player player)) {
                    return;
                }
                WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
                if (!(warlordsEntity instanceof WarlordsPlayer warlordsPlayer)
                        || warlordsPlayer.getGame() != game
                        || warlordsPlayer.isDead()
                        || completed
                        || failed
                        || bossPhase
                        || bossSpawnDelayTicks > 0) {
                    return;
                }
                if (!fragment.unlocked) {
                    player.sendActionBar(Component.text("Defeat this fragment's guardians before picking it up.", NamedTextColor.RED));
                    return;
                }
                if (carriers.containsKey(warlordsPlayer.getUuid())) {
                    player.sendActionBar(Component.text("You are already carrying a timeline fragment.", NamedTextColor.RED));
                    return;
                }
                assignCarrier(fragment, warlordsPlayer, player);
            }

            @EventHandler(ignoreCancelled = true)
            public void onInventoryPickup(InventoryPickupItemEvent event) {
                if (getFragmentByDrop(event.getItem()) != null) {
                    event.setCancelled(true);
                }
            }

            @EventHandler(ignoreCancelled = true)
            public void onDrop(PlayerDropItemEvent event) {
                if (isCarrier(event.getPlayer()) && isFragment(event.getItemDrop().getItemStack())) {
                    event.setCancelled(true);
                }
            }

            @EventHandler(ignoreCancelled = true)
            public void onSwap(PlayerSwapHandItemsEvent event) {
                if (isCarrier(event.getPlayer()) && (isFragment(event.getMainHandItem()) || isFragment(event.getOffHandItem()))) {
                    event.setCancelled(true);
                }
            }

            @EventHandler(ignoreCancelled = true)
            public void onInventoryClick(InventoryClickEvent event) {
                if (!(event.getWhoClicked() instanceof Player player) || !isCarrier(player)) {
                    return;
                }
                if (isFragment(event.getCurrentItem()) || isFragment(event.getCursor())) {
                    event.setCancelled(true);
                }
            }
        });

        game.registerGameMarker(ScoreboardHandler.class, objectiveScoreboardHandler = new SimpleScoreboardHandler(5, "endless_paradox") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return getObjectiveScoreboard();
            }
        });
    }

    @Override
    protected boolean[] getCacheEligibility() {
        return cacheEligibility;
    }

    @Override
    protected boolean handleSpecialDeath(WarlordsDeathEvent event) {
        WarlordsEntity dead = event.getWarlordsEntity();
        if (dead instanceof WarlordsPlayer warlordsPlayer && carriers.containsKey(warlordsPlayer.getUuid())) {
            shatterCarriedFragment(warlordsPlayer, warlordsPlayer.getName() + " fell, and the timeline fragment shattered.");
            return true;
        }
        if (!(dead instanceof WarlordsNPC warlordsNPC)) {
            return false;
        }
        AbstractMob mob = warlordsNPC.getMob();
        if (mob == null) {
            return false;
        }
        for (TimelineFragment fragment : fragments) {
            if (!fragment.guards.remove(mob)) {
                continue;
            }
            if (fragment.guards.isEmpty() && fragment.carrier == null) {
                unlockFragment(fragment);
            }
            break;
        }
        return false;
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                beginCollection();
            }
        }.runTaskLater(START_DELAY_TICKS);

        new GameRunnable(game) {
            @Override
            public void run() {
                if (completed || failed) {
                    cancel();
                    return;
                }
                incrementTicks();
                if (objectiveScoreboardHandler != null && getTicksElapsed() % 20 == 0) {
                    objectiveScoreboardHandler.markChanged();
                }
                if (preparationTicks > 0) {
                    preparationTicks--;
                    return;
                }
                if (bossSpawnDelayTicks > 0) {
                    bossSpawnDelayTicks--;
                    if (getTicksElapsed() % 5 == 0) {
                        showAltarParticles();
                    }
                    if (bossSpawnDelayTicks <= 0) {
                        startBossPhase();
                    }
                    return;
                }
                if (bossPhase) {
                    mobTick();
                    if (handleActiveBossPhase()) {
                        return;
                    }
                    finishBoss();
                    return;
                }
                if (!collectionStarted) {
                    return;
                }

                handleAltarCharges();
                if (completed || failed || bossPhase || bossSpawnDelayTicks > 0) {
                    return;
                }

                collectionTicksRemaining--;
                if (collectionTicksRemaining <= 0) {
                    failCollection("The timeline collapsed before every fragment could be restored.");
                    return;
                }
                if (collectionTicksRemaining % (30 * GameRunnable.SECOND) == 0) {
                    announce(Component.text(getCollectionSecondsRemaining() + " seconds remain to restore the timeline.", NamedTextColor.YELLOW));
                }

                mobTick();
                if (getTicksElapsed() % 5 == 0) {
                    showAltarParticles();
                    showFragmentParticles();
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void beginCollection() {
        if (completed || failed || collectionStarted) {
            return;
        }
        collectionStarted = true;
        collectionTicksRemaining = COLLECTION_DURATION_TICKS;
        fragmentsRequired = BASE_FRAGMENT_COUNT + playerCount();
        for (int i = 0; i < fragmentsRequired; i++) {
            spawnFragment();
        }
        announce(Component.text("Timeline fragments have scattered around the altar.", NamedTextColor.GOLD));
        announce(Component.text("Defeat their guardians, carry each shard to the altar, and charge it for 15 seconds.", NamedTextColor.AQUA));
        announce(Component.text("You have 3 minutes. Dying shatters a carried fragment.", NamedTextColor.RED));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1));
        markObjectiveScoreboardChanged();
    }

    private void spawnFragment() {
        Location location = findScatterLocation();
        TimelineFragment fragment = new TimelineFragment(location);
        fragment.drop = spawnFragmentDrop(location);
        spawnGuards(fragment);
        fragments.add(fragment);
    }

    private Item spawnFragmentDrop(Location location) {
        Item drop = location.getWorld().dropItem(location.clone().add(0, .35, 0), FRAGMENT_ITEM.clone());
        drop.setVelocity(new Vector());
        drop.setGravity(false);
        drop.setPickupDelay(Integer.MAX_VALUE);
        drop.setUnlimitedLifetime(true);
        drop.setGlowing(true);
        drop.setInvulnerable(true);
        return drop;
    }

    private void spawnGuards(TimelineFragment fragment) {
        int guardCount = BASE_GUARDS_PER_FRAGMENT + playerCount();
        for (int i = 0; i < guardCount; i++) {
            Location spawnLocation = findOpenGroundAround(fragment.location, GUARD_SPAWN_MIN_DISTANCE, GUARD_SPAWN_MAX_DISTANCE);
            if (spawnLocation == null) {
                spawnLocation = fragment.location.clone();
            }
            AbstractMob mob = currentAnomaly.getMobSet(0).createMob(spawnLocation);
            fragment.guards.add(mob);
            spawnNewMob(mob, Team.RED);
        }
    }

    private Location findScatterLocation() {
        for (int attempt = 0; attempt < MAX_SCATTER_ATTEMPTS; attempt++) {
            double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
            double distance = ThreadLocalRandom.current().nextDouble(MIN_SCATTER_RADIUS, MAX_SCATTER_RADIUS);
            double x = altarLocation.getX() + Math.cos(angle) * distance;
            double z = altarLocation.getZ() + Math.sin(angle) * distance;
            Location candidate = findOpenGroundLocation(x, z);
            if (candidate != null && isValidScatterLocation(candidate)) {
                return candidate;
            }
        }
        Location fallback = findOpenGroundAround(altarLocation, MIN_SCATTER_RADIUS, MAX_SCATTER_RADIUS);
        if (fallback != null) {
            return fallback;
        }
        Location altarGround = findOpenGroundLocation(altarLocation.getX(), altarLocation.getZ());
        return altarGround != null ? altarGround : findNearestAirAbove(altarLocation);
    }

    private Location findNearestAirAbove(Location origin) {
        World world = origin.getWorld();
        int x = origin.getBlockX();
        int z = origin.getBlockZ();
        for (int y = origin.getBlockY(); y < world.getMaxHeight() - 1; y++) {
            if (isSpawnableSpace(world.getBlockAt(x, y, z)) && isSpawnableSpace(world.getBlockAt(x, y + 1, z))) {
                return new Location(world, x + 0.5, y, z + 0.5);
            }
        }
        return new Location(world, x + 0.5, origin.getY() + 1, z + 0.5);
    }

    private Location findOpenGroundAround(Location center, double minRadius, double maxRadius) {
        for (int attempt = 0; attempt < MAX_SCATTER_ATTEMPTS; attempt++) {
            double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
            double distance = ThreadLocalRandom.current().nextDouble(minRadius, maxRadius);
            Location candidate = findOpenGroundLocation(
                    center.getX() + Math.cos(angle) * distance,
                    center.getZ() + Math.sin(angle) * distance
            );
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    @Nullable
    private Location findOpenGroundLocation(double x, double z) {
        World world = altarLocation.getWorld();
        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);
        int startY = altarLocation.getBlockY();
        int minY = Math.max(world.getMinHeight() + 1, startY - GROUND_SEARCH_RANGE);
        int maxY = Math.min(world.getMaxHeight() - 2, startY + GROUND_SEARCH_RANGE);
        for (int delta = 0; delta <= GROUND_SEARCH_RANGE; delta++) {
            int up = startY + delta;
            if (up <= maxY && isOpenSpawnSpace(world, blockX, up, blockZ)) {
                return new Location(world, blockX + 0.5, up, blockZ + 0.5);
            }
            int down = startY - delta;
            if (delta != 0 && down >= minY && isOpenSpawnSpace(world, blockX, down, blockZ)) {
                return new Location(world, blockX + 0.5, down, blockZ + 0.5);
            }
        }
        return null;
    }

    private boolean isOpenSpawnSpace(World world, int x, int y, int z) {
        Block feet = world.getBlockAt(x, y, z);
        Block head = world.getBlockAt(x, y + 1, z);
        Block ground = world.getBlockAt(x, y - 1, z);
        return isSpawnableSpace(feet) && isSpawnableSpace(head) && ground.getType().isSolid();
    }

    private boolean isSpawnableSpace(Block block) {
        return block.isPassable() && !block.getType().isSolid() && !block.isLiquid();
    }

    private boolean isValidScatterLocation(Location candidate) {
        if (candidate.getWorld() != altarLocation.getWorld()) {
            return false;
        }
        if (!isOpenSpawnSpace(candidate.getWorld(), candidate.getBlockX(), candidate.getBlockY(), candidate.getBlockZ())) {
            return false;
        }
        if (candidate.distanceSquared(altarLocation) < MIN_SCATTER_RADIUS * MIN_SCATTER_RADIUS) {
            return false;
        }
        for (TimelineFragment fragment : fragments) {
            if (fragment.location.getWorld() == candidate.getWorld()
                    && fragment.location.distanceSquared(candidate) < MIN_FRAGMENT_SEPARATION_SQUARED) {
                return false;
            }
        }
        return true;
    }

    private void assignCarrier(TimelineFragment fragment, WarlordsPlayer warlordsPlayer, Player player) {
        if (completed || failed || bossPhase || bossSpawnDelayTicks > 0 || fragment.carrier != null || !fragment.unlocked) {
            return;
        }
        removeFragmentDrop(fragment);
        fragment.carrier = warlordsPlayer;
        ItemStack previousSlotEight = player.getInventory().getItem(8) == null ? null : player.getInventory().getItem(8).clone();
        player.getInventory().setItem(8, FRAGMENT_ITEM.clone());
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false, true));
        carriers.put(warlordsPlayer.getUuid(), new CarrierState(fragment, previousSlotEight));

        announce(Component.text(warlordsPlayer.getName() + " recovered a timeline fragment!", NamedTextColor.GOLD));
        announce(Component.text("Charge it at the altar for 15 seconds.", NamedTextColor.AQUA));
        game.forEachOnlinePlayer((onlinePlayer, team) -> onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1.35f));
        markObjectiveScoreboardChanged();
    }

    private void handleAltarCharges() {
        Iterator<Map.Entry<UUID, CarrierState>> iterator = carriers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, CarrierState> entry = iterator.next();
            CarrierState state = entry.getValue();
            WarlordsPlayer carrier = state.fragment.carrier;
            if (carrier == null || carrier.isDead() || !(carrier.getEntity() instanceof Player player)) {
                continue;
            }
            boolean insideAltar = isInsideAltar(carrier.getLocation());
            if (!insideAltar) {
                if (state.charging) {
                    state.charging = false;
                    state.chargeTicks = 0;
                    announce(Component.text(carrier.getName() + "'s fragment charge was interrupted. Return to the altar.", NamedTextColor.RED));
                    game.forEachOnlinePlayer((onlinePlayer, team) -> onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.5f, .8f));
                }
                continue;
            }
            if (!state.charging) {
                state.charging = true;
                state.chargeTicks = 0;
                announce(Component.text(carrier.getName() + " is charging a timeline fragment at the altar.", NamedTextColor.GOLD));
                game.forEachOnlinePlayer((onlinePlayer, team) -> onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1.15f));
            }
            state.chargeTicks++;
            if (getTicksElapsed() % 5 == 0) {
                showChargeParticles(state);
            }
            if (state.chargeTicks % GameRunnable.SECOND == 0 && state.chargeTicks < ALTAR_CHARGE_TICKS) {
                player.sendActionBar(Component.text("Charging timeline: ", NamedTextColor.LIGHT_PURPLE)
                        .append(Component.text(getChargeSecondsRemaining(state) + "s remaining", NamedTextColor.YELLOW)));
            }
            if (state.chargeTicks >= ALTAR_CHARGE_TICKS) {
                iterator.remove();
                completeFragmentCharge(state);
                if (completed || failed || bossPhase) {
                    return;
                }
            }
        }
    }

    private void completeFragmentCharge(CarrierState state) {
        TimelineFragment fragment = state.fragment;
        fragments.remove(fragment);
        clearCarrierVisuals(fragment.carrier, state);
        fragment.carrier = null;
        fragmentsDelivered++;
        updateFragmentCaches();
        EndlessParadoxCurrencyOption.grantFragmentReward(game);

        announce(Component.text("A timeline fragment has been restored! (" + fragmentsDelivered + "/" + fragmentsRequired + ")", NamedTextColor.GREEN));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2, 1.4f));
        markObjectiveScoreboardChanged();

        if (fragmentsDelivered >= fragmentsRequired) {
            beginBossSpawnDelay();
        }
    }

    private void updateFragmentCaches() {
        if (fragmentsDelivered * 2 >= fragmentsRequired) {
            cacheEligibility[0] = true;
        }
        if (fragmentsDelivered >= fragmentsRequired) {
            cacheEligibility[1] = true;
        }
    }

    private void shatterCarriedFragment(WarlordsPlayer warlordsPlayer, String summary) {
        CarrierState state = carriers.remove(warlordsPlayer.getUuid());
        if (state == null || completed || failed || bossPhase || bossSpawnDelayTicks > 0) {
            return;
        }
        TimelineFragment fragment = state.fragment;
        fragments.remove(fragment);
        clearCarrierVisuals(warlordsPlayer, state);
        clearFragmentGuards(fragment);
        removeFragmentDrop(fragment);

        announce(Component.text(summary, NamedTextColor.RED));
        announce(Component.text("A replacement fragment has manifested.", NamedTextColor.AQUA));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, .6f));
        spawnFragment();
        markObjectiveScoreboardChanged();
    }

    private void unlockFragment(TimelineFragment fragment) {
        fragment.unlocked = true;
        if (fragment.drop == null || !fragment.drop.isValid()) {
            fragment.drop = spawnFragmentDrop(fragment.location);
        }
        fragment.drop.setPickupDelay(0);
        announce(Component.text("A timeline fragment is unguarded. Recover it and charge it at the altar.", NamedTextColor.GREEN));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2, 1.2f));
        markObjectiveScoreboardChanged();
    }

    private void beginBossSpawnDelay() {
        if (bossPhase || completed || failed || bossSpawnDelayTicks > 0) {
            return;
        }
        bossSpawnDelayTicks = BOSS_SPAWN_DELAY_TICKS;
        clearAllFragments();
        clearHostileMobs();
        announce(Component.text("The timeline is whole. Illumina arrives in 3 seconds!", NamedTextColor.GOLD));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, .7f));
        markObjectiveScoreboardChanged();
    }

    private void startBossPhase() {
        if (bossPhase || completed || failed) {
            return;
        }
        bossPhase = true;
        bossSpawnDelayTicks = 0;
        clearAllFragments();
        clearHostileMobs();

        Location spawnLocation = altarLocation.clone().add(0, 1, 0);
        activeBoss = Mob.ILLUMINA.createMob(spawnLocation);
        spawnNewMob(activeBoss, Team.RED);

        announce(Component.text("Defeat Illumina to seal the paradox!", NamedTextColor.GOLD));
        Utils.playGlobalSound(spawnLocation, Sound.ENTITY_WITHER_SPAWN, 2, .75f);
        markObjectiveScoreboardChanged();
    }

    private boolean handleActiveBossPhase() {
        if (activeBoss == null) {
            return false;
        }
        WarlordsNPC bossNpc = activeBoss.getWarlordsNPC();
        return bossNpc != null && bossNpc.isAlive();
    }

    private void finishBoss() {
        cacheEligibility[1] = true;
        cacheEligibility[2] = true;
        EndlessParadoxCurrencyOption.grantBossReward(game);
        activeBoss = null;
        finishAnomaly(cacheEligibility, "The Endless Paradox was sealed.");
    }

    private void failCollection(String summary) {
        if (completed || failed) {
            return;
        }
        failed = true;
        clearAllFragments();
        clearHostileMobs();
        announce(Component.text(summary + " The anomaly has failed.", NamedTextColor.RED));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 2, .7f));
        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, Team.RED));
        markObjectiveScoreboardChanged();
    }

    private List<Component> getObjectiveScoreboard() {
        if (failed) {
            return List.of(Component.text("Timeline Collapsed", NamedTextColor.RED));
        }
        if (completed) {
            return List.of(Component.text("Paradox Sealed", NamedTextColor.GREEN));
        }
        if (!collectionStarted) {
            int seconds = Math.max(0, (preparationTicks + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
            return List.of(Component.text("Anomaly starts in: ", NamedTextColor.WHITE)
                    .append(Component.text(seconds + "s", NamedTextColor.YELLOW)));
        }
        if (bossSpawnDelayTicks > 0) {
            int seconds = Math.max(0, (bossSpawnDelayTicks + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
            return List.of(
                    Component.text("Fragments: ", NamedTextColor.WHITE)
                            .append(Component.text(fragmentsDelivered + "/" + fragmentsRequired, NamedTextColor.GREEN)),
                    Component.text("Illumina arrives in: ", NamedTextColor.WHITE)
                            .append(Component.text(seconds + "s", NamedTextColor.RED))
            );
        }
        if (bossPhase) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.text("Fragments: ", NamedTextColor.WHITE)
                    .append(Component.text(fragmentsDelivered + "/" + fragmentsRequired, NamedTextColor.GREEN)));
            String bossName = activeBoss != null && activeBoss.getWarlordsNPC() != null
                    ? activeBoss.getWarlordsNPC().getName()
                    : "Illumina";
            lines.add(Component.text("Boss: ", NamedTextColor.WHITE).append(Component.text(bossName, NamedTextColor.RED)));
            if (activeBoss != null && activeBoss.getWarlordsNPC() != null) {
                WarlordsNPC bossNpc = activeBoss.getWarlordsNPC();
                lines.add(Component.text("Health: ", NamedTextColor.WHITE)
                        .append(Component.text("❤ " + Math.round(bossNpc.getCurrentHealth()), NamedTextColor.RED))
                        .append(Component.text(" / " + Math.round(bossNpc.getMaxHealth()), NamedTextColor.WHITE)));
            }
            return lines;
        }

        int charging = 0;
        for (CarrierState state : carriers.values()) {
            if (state.charging) {
                charging++;
            }
        }
        return List.of(
                Component.text("Time: ", NamedTextColor.WHITE)
                        .append(Component.text(getCollectionSecondsRemaining() + "s", NamedTextColor.YELLOW)),
                Component.text("Fragments: ", NamedTextColor.WHITE)
                        .append(Component.text(fragmentsDelivered + "/" + fragmentsRequired, NamedTextColor.AQUA)),
                Component.text("Carrying: ", NamedTextColor.WHITE)
                        .append(Component.text(String.valueOf(carriers.size()), NamedTextColor.GOLD)),
                Component.text("Charging: ", NamedTextColor.WHITE)
                        .append(Component.text(charging + " at altar", charging > 0 ? NamedTextColor.GREEN : NamedTextColor.GRAY)),
                Component.text("Caches: ", NamedTextColor.WHITE)
                        .append(Component.text(countCaches() + "/3", NamedTextColor.GREEN))
        );
    }

    private int countCaches() {
        int caches = 0;
        for (boolean earned : cacheEligibility) {
            if (earned) {
                caches++;
            }
        }
        return caches;
    }

    private int getCollectionSecondsRemaining() {
        return Math.max(0, (collectionTicksRemaining + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
    }

    private int getChargeSecondsRemaining(CarrierState state) {
        return Math.max(0, (ALTAR_CHARGE_TICKS - state.chargeTicks + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
    }

    private void showAltarParticles() {
        Location center = altarLocation.clone().add(0, 1, 0);
        EffectUtils.displayParticle(Particle.ENCHANT, center, 10, .8, .8, .8, .02);
        for (int i = 0; i < 12; i++) {
            double angle = Math.PI * 2 * i / 12.0 + getTicksElapsed() * .04;
            Location point = altarLocation.clone().add(Math.cos(angle) * 3.2, .2, Math.sin(angle) * 3.2);
            EffectUtils.displayParticle(Particle.DUST, point, 1, ALTAR_DUST);
        }
    }

    private void showFragmentParticles() {
        for (TimelineFragment fragment : fragments) {
            Location location = fragment.carrier != null
                    ? fragment.carrier.getLocation().clone().add(0, 1.2, 0)
                    : fragment.drop != null && fragment.drop.isValid()
                    ? fragment.drop.getLocation().clone().add(0, .4, 0)
                    : fragment.location.clone().add(0, .5, 0);
            if (fragment.unlocked || fragment.carrier != null) {
                EffectUtils.displayParticle(Particle.END_ROD, location, 4, .25, .35, .25, .01);
            } else {
                EffectUtils.displayParticle(Particle.SMOKE, location, 4, .2, .3, .2, .01);
            }
        }
    }

    private void showChargeParticles(CarrierState state) {
        double progress = state.chargeTicks / (double) ALTAR_CHARGE_TICKS;
        double radius = 2.4 + progress * .8;
        for (int i = 0; i < 14; i++) {
            double angle = Math.PI * 2 * i / 14.0 + getTicksElapsed() * .05;
            Location point = altarLocation.clone().add(Math.cos(angle) * radius, .2, Math.sin(angle) * radius);
            EffectUtils.displayParticle(Particle.DUST, point, 1, CHARGE_DUST);
        }
        EffectUtils.displayParticle(Particle.END_ROD, altarLocation.clone().add(0, 1.2, 0), 6, .4, .6, .4, .02);
    }

    private boolean isInsideAltar(Location location) {
        return location.getWorld() != null
                && location.getWorld().equals(altarLocation.getWorld())
                && location.distanceSquared(altarLocation) <= ALTAR_RADIUS_SQUARED;
    }

    private TimelineFragment getFragmentByDrop(Item item) {
        for (TimelineFragment fragment : fragments) {
            if (fragment.drop != null && fragment.drop.getUniqueId().equals(item.getUniqueId())) {
                return fragment;
            }
        }
        return null;
    }

    private void clearAllFragments() {
        for (TimelineFragment fragment : List.copyOf(fragments)) {
            clearFragmentGuards(fragment);
            removeFragmentDrop(fragment);
        }
        fragments.clear();
        for (CarrierState state : List.copyOf(carriers.values())) {
            clearCarrierVisuals(state.fragment.carrier, state);
        }
        carriers.clear();
    }

    private void clearFragmentGuards(TimelineFragment fragment) {
        for (AbstractMob guard : List.copyOf(fragment.guards)) {
            removeHostileMob(guard);
        }
        fragment.guards.clear();
    }

    private void removeFragmentDrop(TimelineFragment fragment) {
        if (fragment.drop == null) {
            return;
        }
        fragment.drop.remove();
        fragment.drop = null;
    }

    private void clearCarrierVisuals(@Nullable WarlordsPlayer carrier, CarrierState state) {
        if (carrier == null || !(carrier.getEntity() instanceof Player player)) {
            return;
        }
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.getInventory().setItem(8, state.previousSlotEight);
    }

    private void markObjectiveScoreboardChanged() {
        if (objectiveScoreboardHandler != null) {
            objectiveScoreboardHandler.markChanged();
        }
    }

    private boolean isCarrier(Player player) {
        return carriers.containsKey(player.getUniqueId());
    }

    private boolean isFragment(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.isSimilar(FRAGMENT_ITEM);
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        super.updateInventory(warlordsPlayer, player);
        if (carriers.containsKey(warlordsPlayer.getUuid())) {
            player.getInventory().setItem(8, FRAGMENT_ITEM.clone());
        }
    }

    @Override
    public void onPlayerQuit(Player player) {
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (warlordsEntity instanceof WarlordsPlayer warlordsPlayer && carriers.containsKey(warlordsPlayer.getUuid()) && !completed && !failed) {
            shatterCarriedFragment(warlordsPlayer, warlordsPlayer.getName() + " left, and the timeline fragment shattered.");
        }
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        clearAllFragments();
        activeBoss = null;
        super.onGameCleanup(game);
    }

    private static final class TimelineFragment {
        private final Location location;
        private final Set<AbstractMob> guards = ConcurrentHashMap.newKeySet();
        private Item drop;
        private boolean unlocked;
        private WarlordsPlayer carrier;

        private TimelineFragment(Location location) {
            this.location = location;
        }
    }

    private static final class CarrierState {
        private final TimelineFragment fragment;
        private final ItemStack previousSlotEight;
        private int chargeTicks;
        private boolean charging;

        private CarrierState(TimelineFragment fragment, ItemStack previousSlotEight) {
            this.fragment = fragment;
            this.previousSlotEight = previousSlotEight;
        }
    }
}

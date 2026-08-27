package com.ebicep.warlords.game.option.pve.anomaly;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class DunestarEscortOption extends AbstractAnomalyOption {

    private static final int SEGMENT_DURATION_TICKS = 120 * GameRunnable.SECOND;
    private static final int CHECKPOINT_CHARGE_TICKS = 30 * GameRunnable.SECOND;
    private static final int MOB_SPAWN_INTERVAL = 10; // ticks
    private static final int LASER_INTERVAL_TICKS = 15 * GameRunnable.SECOND;
    private static final int LASER_TELEGRAPH_TICKS = 2 * GameRunnable.SECOND;
    private static final float ABILITY_COOLDOWN_MULTIPLIER = 3;
    private static final double CHECKPOINT_RADIUS_SQUARED = 25;
    private static final double FRONT_SPAWN_CHANCE = .8;
    private static final double FRONT_SPAWN_HALF_ANGLE = Math.PI / 2;
    private static final double LASER_RANGE = 40;
    private static final double LASER_WIDTH = 2.5;
    private static final double LASER_VERTICAL_HALF = 3;
    private static final double LASER_MAX_OFFSET = 4;
    private static final Particle.DustOptions LASER_TELEGRAPH_DUST = new Particle.DustOptions(Color.fromRGB(255, 70, 70), 3f);
    private static final ItemStack RELIC_ITEM = new ItemBuilder(Material.HEART_OF_THE_SEA)
            .name(Component.text("Dunestar Relic", NamedTextColor.AQUA))
            .lore(
                    Component.text("Carry this relic to the sanctuary.", NamedTextColor.GRAY),
                    Component.text("Primary skill disabled. Other abilities have triple cooldowns.", NamedTextColor.RED),
                    Component.text("You cannot deal damage.", NamedTextColor.RED)
            )
            .glow()
            .get();

    private final boolean[] cacheEligibility = new boolean[3];
    private final Set<GameRunnable> laserTasks = ConcurrentHashMap.newKeySet();
    private final List<FloatModifiable.FloatModifier> carrierCooldownModifiers = new ArrayList<>();
    private final List<UUID> rewardEligiblePlayers = new ArrayList<>();

    private List<DunestarRouteMarker> routeMarkers = List.of();
    private WarlordsPlayer carrier;
    private Item relicDrop;
    private ItemStack previousSlotEight;
    private int preparationTicks = START_DELAY_TICKS;
    private int nextRouteIndex = 1;
    private int escortTicks;
    private int segmentTicksRemaining;
    private int laserTicksRemaining;
    private int checkpointChargeTicks;
    private boolean relicSpawned;
    private boolean chargingCheckpoint;
    private boolean failed;

    @Override
    public void register(@Nonnull Game game) {
        super.register(game);
        routeMarkers = game.getMarkers(DunestarRouteMarker.class)
                .stream()
                .sorted(Comparator.comparingInt(DunestarRouteMarker::getRouteIndex))
                .toList();
        if (routeMarkers.size() != 4) {
            throw new IllegalStateException("Plains of Dunestar requires exactly four route markers");
        }

        game.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
            public void onDamage(WarlordsDamageHealingEvent event) {
                if (carrier != null
                        && event.isDamageInstance()
                        && event.getSource() == carrier
                        && event.getWarlordsEntity() != carrier
                ) {
                    event.setCancelled(true);
                }
            }

            @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
            public void onAbility(WarlordsAbilityActivateEvent.Pre event) {
                if (carrier == null || event.getWarlordsEntity() != carrier || event.getSlot() != 0) {
                    return;
                }
                event.setCancelled(true);
                event.getPlayer().sendActionBar(Component.text("The relic prevents you from using your primary skill.", NamedTextColor.RED));
            }

            @EventHandler(ignoreCancelled = true)
            public void onJump(PlayerJumpEvent event) {
                if (isCarrier(event.getPlayer())) {
                    event.setCancelled(true);
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onRelicPickup(EntityPickupItemEvent event) {
                if (relicDrop == null || !event.getItem().getUniqueId().equals(relicDrop.getUniqueId())) {
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
                        || carrier != null) {
                    return;
                }
                assignCarrier(warlordsPlayer, player);
            }

            @EventHandler(ignoreCancelled = true)
            public void onDrop(PlayerDropItemEvent event) {
                if (isCarrier(event.getPlayer()) && event.getItemDrop().getItemStack().isSimilar(RELIC_ITEM)) {
                    event.setCancelled(true);
                }
            }

            @EventHandler(ignoreCancelled = true)
            public void onSwap(PlayerSwapHandItemsEvent event) {
                if (isCarrier(event.getPlayer()) && (isRelic(event.getMainHandItem()) || isRelic(event.getOffHandItem()))) {
                    event.setCancelled(true);
                }
            }

            @EventHandler(ignoreCancelled = true)
            public void onInventoryClick(InventoryClickEvent event) {
                if (!(event.getWhoClicked() instanceof Player player) || !isCarrier(player)) {
                    return;
                }
                if (isRelic(event.getCurrentItem()) || isRelic(event.getCursor())) {
                    event.setCancelled(true);
                }
            }
        });

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(5, "dunestar_escort") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return getEscortScoreboard();
            }
        });
    }

    @Override
    protected boolean handleSpecialDeath(WarlordsDeathEvent event) {
        if (carrier == null || event.getWarlordsEntity() != carrier) {
            return false;
        }
        failEscort("The relic carrier fell.");
        return true;
    }

    @Override
    public void start(@Nonnull Game game) {
        rewardEligiblePlayers.clear();
        game.warlordsPlayersWithoutSpectators()
                .filter(entry -> entry.getValue() == Team.BLUE)
                .map(Map.Entry::getKey)
                .forEach(rewardEligiblePlayers::add);

        new GameRunnable(game) {
            @Override
            public void run() {
                spawnRelic();
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
                if (preparationTicks > 0) {
                    preparationTicks--;
                }
                if (carrier == null) {
                    if (relicSpawned && (relicDrop == null || !relicDrop.isValid()) && getTicksElapsed() % GameRunnable.SECOND == 0) {
                        spawnRelic();
                    }
                    if (relicDrop != null && relicDrop.isValid() && getTicksElapsed() % 10 == 0) {
                        showRelicParticles();
                    }
                    return;
                }
                if (carrier.isDead() || !(carrier.getEntity() instanceof Player)) {
                    failEscort("The relic carrier was lost.");
                    return;
                }

                escortTicks++;
                mobTick();

                if (escortTicks % 5 == 0) {
                    showRouteParticles();
                }

                handleCheckpointCharge();
                if (completed || failed || carrier == null) {
                    return;
                }

                if (!chargingCheckpoint) {
                    segmentTicksRemaining--;
                    if (segmentTicksRemaining <= 0) {
                        failEscort("The relic did not reach " + getNextDestinationName() + " in time.");
                        return;
                    }
                    if (segmentTicksRemaining % (30 * GameRunnable.SECOND) == 0) {
                        announce(Component.text(getSecondsRemaining() + " seconds remain to reach " + getNextDestinationName() + ".", NamedTextColor.YELLOW));
                    }
                }

                laserTicksRemaining--;
                if (laserTicksRemaining <= 0) {
                    startLaserHazard();
                    laserTicksRemaining = LASER_INTERVAL_TICKS;
                }

                if (escortTicks % MOB_SPAWN_INTERVAL == 0) {
                    if (mobCount() < getMaximumMobCount()) {
                        spawnAroundCarrier();
                    }
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void spawnRelic() {
        if (completed || failed || carrier != null || relicDrop != null && relicDrop.isValid()) {
            return;
        }
        relicSpawned = true;
        Location location = routeMarkers.get(0).getLocation().clone();
        relicDrop = location.getWorld().dropItem(location, RELIC_ITEM.clone());
        relicDrop.setVelocity(new Vector());
        relicDrop.setGravity(false);
        relicDrop.setPickupDelay(0);
        relicDrop.setGlowing(true);
        relicDrop.setInvulnerable(true);

        announce(Component.text("The Dunestar Relic has appeared at the starting pedestal.", NamedTextColor.GOLD));
        announce(Component.text("Choose a carrier by picking up the relic.", NamedTextColor.AQUA));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1));
    }

    private void assignCarrier(WarlordsPlayer warlordsPlayer, Player player) {
        if (completed || failed || carrier != null) {
            return;
        }
        removeRelicDrop();
        carrier = warlordsPlayer;
        escortTicks = 0;
        segmentTicksRemaining = SEGMENT_DURATION_TICKS;
        laserTicksRemaining = LASER_INTERVAL_TICKS;
        checkpointChargeTicks = 0;
        chargingCheckpoint = false;
        previousSlotEight = player.getInventory().getItem(8) == null ? null : player.getInventory().getItem(8).clone();
        player.getInventory().setItem(8, RELIC_ITEM.clone());
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false, true));

        List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
        for (int i = 1; i < abilities.size(); i++) {
            carrierCooldownModifiers.add(abilities.get(i).getCooldown().addModifier(
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                    "Dunestar Relic",
                    ABILITY_COOLDOWN_MULTIPLIER
            ));
        }

        announce(Component.text(carrier.getName() + " picked up the Dunestar Relic!", NamedTextColor.GOLD));
        announce(Component.text("Reach each destination within 2 minutes, then charge the relic energy for 30 seconds.", NamedTextColor.AQUA));
        game.forEachOnlinePlayer((onlinePlayer, team) -> onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1));
    }

    private void handleCheckpointCharge() {
        if (carrier == null || nextRouteIndex >= routeMarkers.size()) {
            return;
        }

        Location target = routeMarkers.get(nextRouteIndex).getLocation();
        boolean insideCheckpoint = target.getWorld().equals(carrier.getLocation().getWorld())
                && carrier.getLocation().distanceSquared(target) <= CHECKPOINT_RADIUS_SQUARED;

        if (!chargingCheckpoint) {
            if (!insideCheckpoint) {
                return;
            }
            chargingCheckpoint = true;
            checkpointChargeTicks = 0;
            announce(Component.text(getNextDestinationName() + " reached. Hold the relic here for 30 seconds to restore maximum power!", NamedTextColor.GOLD));
            game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1.15f));
        }

        if (!insideCheckpoint) {
            if (checkpointChargeTicks > 0) {
                checkpointChargeTicks = 0;
                announce(Component.text("Relic charge interrupted! Return to " + getNextDestinationName() + " and hold the position.", NamedTextColor.RED));
                game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.5f, .8f));
            }
            return;
        }

        checkpointChargeTicks++;
        if (escortTicks % 5 == 0) {
            showCheckpointChargeParticles();
        }
        if (checkpointChargeTicks % (5 * GameRunnable.SECOND) == 0 && checkpointChargeTicks < CHECKPOINT_CHARGE_TICKS) {
            int secondsRemaining = getCheckpointChargeSecondsRemaining();
            game.forEachOnlinePlayer((player, team) -> player.sendActionBar(
                    Component.text("Relic charging: ", NamedTextColor.AQUA)
                            .append(Component.text(secondsRemaining + "s remaining", NamedTextColor.YELLOW))
            ));
        }
        if (checkpointChargeTicks >= CHECKPOINT_CHARGE_TICKS) {
            completeCheckpointCharge();
        }
    }

    private void completeCheckpointCharge() {
        int completedRouteIndex = nextRouteIndex;
        int cacheIndex = completedRouteIndex - 1;
        cacheEligibility[cacheIndex] = true;
        chargingCheckpoint = false;
        checkpointChargeTicks = 0;

        if (completedRouteIndex < routeMarkers.size() - 1) {
            DunestarCurrencyOption.grantCheckpointReward(game);
            announce(Component.text("Checkpoint " + completedRouteIndex + " fully charged. Dunestar Cache " + (cacheIndex + 1) + " unlocked!", NamedTextColor.GREEN));
            game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1.25f));
            nextRouteIndex++;
            segmentTicksRemaining = SEGMENT_DURATION_TICKS;
            announce(Component.text("Relic power restored. You have 120 seconds to reach " + getNextDestinationName() + ".", NamedTextColor.AQUA));
            return;
        }

        announce(Component.text("The Dunestar Relic is fully charged at the sanctuary!", NamedTextColor.GREEN));
        finishEscort("Escort completed.");
    }

    private void spawnAroundCarrier() {
        Location center = carrier.getLocation();
        World world = center.getWorld();
        double angle;
        Vector facing = center.getDirection().setY(0);
        if (facing.lengthSquared() > 0 && ThreadLocalRandom.current().nextDouble() < FRONT_SPAWN_CHANCE) {
            facing.normalize();
            double facingAngle = Math.atan2(facing.getZ(), facing.getX());
            angle = facingAngle + ThreadLocalRandom.current().nextDouble(-FRONT_SPAWN_HALF_ANGLE, FRONT_SPAWN_HALF_ANGLE);
        } else {
            angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
        }
        double distance = ThreadLocalRandom.current().nextDouble(10, 15);
        double x = center.getX() + Math.cos(angle) * distance;
        double z = center.getZ() + Math.sin(angle) * distance;
        int y = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z)) + 1;
        spawnNewMob(currentAnomaly.getMobSet(nextRouteIndex - 1)
                .createMob(new Location(world, x, y, z)), Team.RED);
    }

    private void startLaserHazard() {
        if (carrier == null || completed || failed) {
            return;
        }
        Location center = carrier.getLocation().clone().add(0, 1, 0);
        List<LaserLine> lines = List.of(createLaserLine(center), createLaserLine(center));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2, 1.2f));

        GameRunnable task = new GameRunnable(game) {
            private int ticks;

            @Override
            public void run() {
                if (completed || failed || carrier == null) {
                    laserTasks.remove(this);
                    cancel();
                    return;
                }
                ticks++;
                lines.forEach(line -> drawLaser(line, false));
                if (ticks < LASER_TELEGRAPH_TICKS) {
                    return;
                }
                lines.forEach(line -> drawLaser(line, true));
                fireLasers(lines);
                laserTasks.remove(this);
                cancel();
            }

            @Override
            public void cancel() {
                laserTasks.remove(this);
                super.cancel();
            }
        };
        laserTasks.add(task);
        task.runTaskTimer(0, 1);
    }

    private LaserLine createLaserLine(Location center) {
        double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
        Vector direction = new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize();
        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX());
        double offset = ThreadLocalRandom.current().nextDouble(-LASER_MAX_OFFSET, LASER_MAX_OFFSET);
        Location lineCenter = center.clone().add(perpendicular.multiply(offset));
        Location start = lineCenter.clone().subtract(direction.clone().multiply(LASER_RANGE));
        Location end = lineCenter.clone().add(direction.multiply(LASER_RANGE));
        return new LaserLine(start, end);
    }

    private void drawLaser(LaserLine line, boolean firing) {
        World world = line.start().getWorld();
        Vector direction = line.end().toVector().subtract(line.start().toVector());
        double length = direction.length();
        Vector unit = direction.normalize();
        double step = firing ? .35 : .7;
        for (double distance = 0; distance <= length; distance += step) {
            Location point = line.start().clone().add(unit.clone().multiply(distance));
            if (firing) {
                world.spawnParticle(Particle.ELECTRIC_SPARK, point, 2, .05, .05, .05, 0);
            } else {
                world.spawnParticle(Particle.DUST, point, 0, LASER_TELEGRAPH_DUST);
            }
        }
        if (firing) {
            world.playSound(line.start(), Sound.ENTITY_WARDEN_SONIC_BOOM, 4, .8f);
        }
    }

    private void fireLasers(List<LaserLine> lines) {
        Set<UUID> hitPlayers = ConcurrentHashMap.newKeySet();
        game.warlordsPlayers().forEach(player -> {
            if (player == carrier || player.isDead() || !player.isOnline()) {
                return;
            }
            for (LaserLine line : lines) {
                if (!isInsideLaser(player.getLocation().clone().add(0, 1, 0), line)) {
                    continue;
                }
                if (!hitPlayers.add(player.getUuid())) {
                    return;
                }
                player.addInstance(InstanceBuilder
                        .damage()
                        .cause("Dunestar Laser")
                        .source(player)
                        .value(player.getMaxHealth() * .25f)
                        .flags(InstanceFlags.TRUE_DAMAGE, InstanceFlags.IGNORE_CRIT_MODIFIERS)
                );
                return;
            }
        });
    }

    private boolean isInsideLaser(Location point, LaserLine line) {
        if (point.getWorld() != line.start().getWorld()) {
            return false;
        }
        if (Math.abs(point.getY() - line.start().getY()) > LASER_VERTICAL_HALF) {
            return false;
        }
        Vector start = line.start().toVector();
        Vector end = line.end().toVector();
        Vector segment = end.clone().subtract(start);
        Vector toPoint = point.toVector().subtract(start);
        double lengthSquared = segment.lengthSquared();
        double projection = lengthSquared == 0 ? 0 : Math.max(0, Math.min(1, toPoint.dot(segment) / lengthSquared));
        Vector closest = start.add(segment.multiply(projection));
        return point.toVector().distanceSquared(closest) <= LASER_WIDTH * LASER_WIDTH;
    }

    private void showRelicParticles() {
        Location location = relicDrop.getLocation().clone().add(0, .5, 0);
        location.getWorld().spawnParticle(Particle.END_ROD, location, 8, .4, .6, .4, .02);
        location.getWorld().spawnParticle(Particle.ENCHANT, location, 8, .5, .7, .5, .02);
    }

    private void showRouteParticles() {
        if (carrier == null || nextRouteIndex >= routeMarkers.size()) {
            return;
        }
        Location carrierLocation = carrier.getLocation().clone().add(0, 1, 0);
        carrierLocation.getWorld().spawnParticle(Particle.ENCHANT, carrierLocation, 5, .4, .7, .4, .02);

        Location target = routeMarkers.get(nextRouteIndex).getLocation().clone().add(0, 1, 0);
        EffectUtils.displayParticle(Particle.END_ROD, target, 6, .8, .8, .8, .02);
    }

    private void showCheckpointChargeParticles() {
        if (nextRouteIndex >= routeMarkers.size()) {
            return;
        }
        Location target = routeMarkers.get(nextRouteIndex).getLocation().clone().add(0, 1, 0);
        double progress = checkpointChargeTicks / (double) CHECKPOINT_CHARGE_TICKS;
        double radius = 2.5 + progress * .8;
        for (int i = 0; i < 12; i++) {
            double angle = Math.PI * 2 * i / 12.0 + escortTicks * .04;
            Location point = target.clone().add(Math.cos(angle) * radius, .15, Math.sin(angle) * radius);
            EffectUtils.displayParticle(Particle.END_ROD, point, 1, 0, 0, 0, 0);
        }
        EffectUtils.displayParticle(Particle.ENCHANT, target, 8, .7, .8, .7, .05);
    }

    private int getMaximumMobCount() {
        return 6 + playerCount() * 4 + nextRouteIndex * 2;
    }

    private String getNextDestinationName() {
        return nextRouteIndex >= routeMarkers.size() - 1
                ? "the sanctuary"
                : "Checkpoint " + nextRouteIndex;
    }

    private int getSecondsRemaining() {
        return Math.max(0, (segmentTicksRemaining + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
    }

    private int getCheckpointChargeSecondsRemaining() {
        return Math.max(0, (CHECKPOINT_CHARGE_TICKS - checkpointChargeTicks + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
    }

    private List<Component> getEscortScoreboard() {
        if (failed) {
            return List.of(Component.text("Escort Failed", NamedTextColor.RED));
        }
        if (completed) {
            return List.of(Component.text("Escort Complete", NamedTextColor.GREEN));
        }
        if (carrier == null) {
            if (preparationTicks > 0) {
                int seconds = Math.max(0, (preparationTicks + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
                return List.of(Component.text("Relic appears in: ", NamedTextColor.WHITE)
                        .append(Component.text(seconds + "s", NamedTextColor.YELLOW)));
            }
            return List.of(
                    Component.text("Relic: ", NamedTextColor.WHITE).append(Component.text("Awaiting Carrier", NamedTextColor.GOLD)),
                    Component.text("Location: ", NamedTextColor.WHITE).append(Component.text("Starting Pedestal", NamedTextColor.AQUA)),
                    Component.text("Action: ", NamedTextColor.WHITE).append(Component.text("Pick up the relic", NamedTextColor.GREEN)),
                    Component.text("Caches: ", NamedTextColor.WHITE).append(Component.text("0/3", NamedTextColor.GREEN))
            );
        }

        String targetName = nextRouteIndex >= routeMarkers.size() - 1
                ? "Sanctuary"
                : "Checkpoint " + nextRouteIndex;
        int distance = nextRouteIndex < routeMarkers.size()
                ? (int) Math.ceil(carrier.getLocation().distance(routeMarkers.get(nextRouteIndex).getLocation()))
                : 0;
        int caches = 0;
        for (boolean earned : cacheEligibility) {
            if (earned) {
                caches++;
            }
        }

        if (chargingCheckpoint) {
            int chargePercent = Math.min(100, (int) Math.floor(checkpointChargeTicks * 100.0 / CHECKPOINT_CHARGE_TICKS));
            return List.of(
                    Component.text("Carrier: ", NamedTextColor.WHITE).append(Component.text(carrier.getName(), NamedTextColor.GOLD)),
                    Component.text("Charging: ", NamedTextColor.WHITE).append(Component.text(targetName, NamedTextColor.AQUA)),
                    Component.text("Charge: ", NamedTextColor.WHITE).append(Component.text(chargePercent + "%", NamedTextColor.GREEN)),
                    Component.text("Caches: ", NamedTextColor.WHITE).append(Component.text(caches + "/3", NamedTextColor.GREEN))
            );
        }

        return List.of(
                Component.text("Carrier: ", NamedTextColor.WHITE).append(Component.text(carrier.getName(), NamedTextColor.GOLD)),
                Component.text("Next: ", NamedTextColor.WHITE).append(Component.text(targetName, NamedTextColor.AQUA)),
                Component.text("Time: ", NamedTextColor.WHITE).append(Component.text(getSecondsRemaining() + "s", NamedTextColor.YELLOW)),
                Component.text("Distance: ", NamedTextColor.WHITE).append(Component.text(distance + "m", NamedTextColor.YELLOW)),
                Component.text("Caches: ", NamedTextColor.WHITE).append(Component.text(caches + "/3", NamedTextColor.GREEN))
        );
    }

    private void finishEscort(String summary) {
        cancelLaserTasks();
        removeRelicDrop();
        clearCarrierState();
        finishAnomaly(cacheEligibility, summary);
    }

    private void failEscort(String summary) {
        if (completed || failed) {
            return;
        }
        failed = true;
        cancelLaserTasks();
        removeRelicDrop();
        clearCarrierState();
        clearHostileMobs();
        grantUnlockedCaches();
        announce(Component.text(summary + " The anomaly has failed.", NamedTextColor.RED));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 2, .7f));
        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, Team.RED));
    }

    private void grantUnlockedCaches() {
        int eligibleObjectiveCount = Math.min(cacheEligibility.length, currentAnomaly.getRewardPools().size());
        rewardEligiblePlayers.forEach(uuid -> {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
            int cachesGranted = 0;
            for (int i = 0; i < eligibleObjectiveCount; i++) {
                if (!cacheEligibility[i]) {
                    continue;
                }
                AnomalyRewardCache cache = currentAnomaly.getRewardPools().get(i)
                        .createCache(featuredLegendarySet, rotationStart);
                databasePlayer.getPveStats().getGameEventRewards().add(cache);
                cachesGranted++;
            }
            if (cachesGranted == 0) {
                return;
            }
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            RewardInventory.sendRewardMessage(
                    uuid,
                    Component.text(cachesGranted + " Anomaly Reward " + (cachesGranted == 1 ? "Cache is" : "Caches are") + " ready to claim.", NamedTextColor.AQUA)
            );
        });
    }

    private void cancelLaserTasks() {
        for (GameRunnable task : List.copyOf(laserTasks)) {
            task.cancel();
        }
        laserTasks.clear();
    }

    private void removeRelicDrop() {
        if (relicDrop == null) {
            return;
        }
        relicDrop.remove();
        relicDrop = null;
    }

    private void clearCarrierState() {
        carrierCooldownModifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
        carrierCooldownModifiers.clear();
        if (carrier == null || !(carrier.getEntity() instanceof Player player)) {
            carrier = null;
            return;
        }
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.getInventory().setItem(8, previousSlotEight);
        carrier = null;
    }

    private boolean isCarrier(Player player) {
        return carrier != null && carrier.getUuid().equals(player.getUniqueId());
    }

    private boolean isRelic(@Nullable ItemStack itemStack) {
        return itemStack != null && itemStack.isSimilar(RELIC_ITEM);
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        super.updateInventory(warlordsPlayer, player);
        if (carrier != null && carrier.getUuid().equals(warlordsPlayer.getUuid())) {
            player.getInventory().setItem(8, RELIC_ITEM.clone());
        }
    }

    @Override
    public void onPlayerQuit(Player player) {
        if (isCarrier(player) && !completed && !failed) {
            failEscort("The relic carrier left the anomaly.");
        }
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        cancelLaserTasks();
        removeRelicDrop();
        clearCarrierState();
        super.onGameCleanup(game);
    }

    private record LaserLine(Location start, Location end) {
    }
}

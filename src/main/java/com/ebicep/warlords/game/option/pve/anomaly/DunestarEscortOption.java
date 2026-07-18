package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DunestarEscortOption extends AbstractAnomalyOption {

    private static final int MOB_SPAWN_INTERVAL = 2 * GameRunnable.SECOND;
    private static final double CHECKPOINT_RADIUS_SQUARED = 25;
    private static final ItemStack RELIC_ITEM = new ItemBuilder(Material.HEART_OF_THE_SEA)
            .name(Component.text("Dunestar Relic", NamedTextColor.AQUA))
            .lore(
                    Component.text("Carry this relic to the sanctuary.", NamedTextColor.GRAY),
                    Component.text("You cannot activate abilities or deal damage.", NamedTextColor.RED)
            )
            .glow()
            .get();

    private final boolean[] cacheEligibility = new boolean[3];

    private List<DunestarRouteMarker> routeMarkers = List.of();
    private WarlordsPlayer carrier;
    private Item relicDrop;
    private ItemStack previousSlotEight;
    private int preparationTicks = START_DELAY_TICKS;
    private int nextRouteIndex = 1;
    private int escortTicks;
    private boolean relicSpawned;

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
                if (carrier != null && event.isDamageInstance() && event.getSource() == carrier) {
                    event.setCancelled(true);
                }
            }

            @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
            public void onAbility(WarlordsAbilityActivateEvent.Pre event) {
                if (carrier == null || event.getWarlordsEntity() != carrier) {
                    return;
                }
                event.setCancelled(true);
                event.getPlayer().sendActionBar(Component.text("The relic prevents you from using abilities.", NamedTextColor.RED));
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
        finishEscort("The relic carrier fell.");
        return true;
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                spawnRelic();
            }
        }.runTaskLater(START_DELAY_TICKS);

        new GameRunnable(game) {
            @Override
            public void run() {
                if (completed) {
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
                    finishEscort("The relic carrier was lost.");
                    return;
                }

                escortTicks++;
                mobTick();
                if (escortTicks % MOB_SPAWN_INTERVAL == 0 && mobCount() < getMaximumMobCount()) {
                    spawnAroundCarrier();
                }
                if (escortTicks % 10 == 0) {
                    showRouteParticles();
                    checkRouteProgress();
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void spawnRelic() {
        if (completed || carrier != null || relicDrop != null && relicDrop.isValid()) {
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
        if (completed || carrier != null) {
            return;
        }
        removeRelicDrop();
        carrier = warlordsPlayer;
        escortTicks = 0;
        previousSlotEight = player.getInventory().getItem(8) == null ? null : player.getInventory().getItem(8).clone();
        player.getInventory().setItem(8, RELIC_ITEM.clone());
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false, true));

        announce(Component.text(carrier.getName() + " picked up the Dunestar Relic!", NamedTextColor.GOLD));
        announce(Component.text("Protect the carrier through two checkpoints and into the sanctuary.", NamedTextColor.AQUA));
        game.forEachOnlinePlayer((onlinePlayer, team) -> onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1));
    }

    private void checkRouteProgress() {
        if (carrier == null || nextRouteIndex >= routeMarkers.size()) {
            return;
        }
        Location target = routeMarkers.get(nextRouteIndex).getLocation();
        if (!target.getWorld().equals(carrier.getLocation().getWorld()) || carrier.getLocation().distanceSquared(target) > CHECKPOINT_RADIUS_SQUARED) {
            return;
        }

        int cacheIndex = nextRouteIndex - 1;
        cacheEligibility[cacheIndex] = true;
        if (nextRouteIndex < routeMarkers.size() - 1) {
            announce(Component.text("Checkpoint " + nextRouteIndex + " reached. Dunestar Cache " + (cacheIndex + 1) + " unlocked!", NamedTextColor.GREEN));
            game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2, 1));
            nextRouteIndex++;
            return;
        }

        announce(Component.text("The Dunestar Relic reached the sanctuary!", NamedTextColor.GREEN));
        finishEscort("Escort completed.");
    }

    private void spawnAroundCarrier() {
        Location center = carrier.getLocation();
        World world = center.getWorld();
        double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
        double distance = ThreadLocalRandom.current().nextDouble(12, 20);
        double x = center.getX() + Math.cos(angle) * distance;
        double z = center.getZ() + Math.sin(angle) * distance;
        int y = world.getHighestBlockYAt((int) Math.floor(x), (int) Math.floor(z)) + 1;
        spawnCurrentAnomalyMob(new Location(world, x, y, z));
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
        target.getWorld().spawnParticle(Particle.END_ROD, target, 6, .8, .8, .8, .02);
    }

    private int getMaximumMobCount() {
        return 6 + playerCount() * 4 + nextRouteIndex * 2;
    }

    private List<Component> getEscortScoreboard() {
        if (completed) {
            return List.of(Component.text("Escort Complete", NamedTextColor.GREEN));
        }
        if (carrier == null) {
            if (preparationTicks > 0) {
                int seconds = Math.max(0, (preparationTicks + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
                return List.of(Component.text("Relic appears in: ", NamedTextColor.GRAY)
                        .append(Component.text(seconds + "s", NamedTextColor.YELLOW)));
            }
            return List.of(
                    Component.text("Relic: ", NamedTextColor.GRAY).append(Component.text("Awaiting Carrier", NamedTextColor.GOLD)),
                    Component.text("Location: ", NamedTextColor.GRAY).append(Component.text("Starting Pedestal", NamedTextColor.AQUA)),
                    Component.text("Action: ", NamedTextColor.GRAY).append(Component.text("Pick up the relic", NamedTextColor.GREEN)),
                    Component.text("Caches: ", NamedTextColor.GRAY).append(Component.text("0/3", NamedTextColor.GREEN))
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

        return List.of(
                Component.text("Carrier: ", NamedTextColor.GRAY).append(Component.text(carrier.getName(), NamedTextColor.GOLD)),
                Component.text("Next: ", NamedTextColor.GRAY).append(Component.text(targetName, NamedTextColor.AQUA)),
                Component.text("Distance: ", NamedTextColor.GRAY).append(Component.text(distance + "m", NamedTextColor.YELLOW)),
                Component.text("Caches: ", NamedTextColor.GRAY).append(Component.text(caches + "/3", NamedTextColor.GREEN))
        );
    }

    private void finishEscort(String summary) {
        removeRelicDrop();
        clearCarrierState();
        finishAnomaly(cacheEligibility, summary);
    }

    private void removeRelicDrop() {
        if (relicDrop == null) {
            return;
        }
        relicDrop.remove();
        relicDrop = null;
    }

    private void clearCarrierState() {
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
        if (isCarrier(player) && !completed) {
            finishEscort("The relic carrier left the anomaly.");
        }
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        removeRelicDrop();
        clearCarrierState();
        super.onGameCleanup(game);
    }
}

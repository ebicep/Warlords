package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DunestarMovingWallOption implements Option, Listener {

    private static final int PANEL_COUNT = 7;
    private static final int DISPLAY_UPDATE_INTERVAL = 2;
    private static final int DAMAGE_INTERVAL = GameRunnable.SECOND;
    private static final double START_DISTANCE = -18;
    private static final double SPEED_PER_TICK = 0.0675;
    private static final double WALL_WIDTH = 35;
    private static final double WALL_HEIGHT = 14;
    private static final double WALL_THICKNESS = 0.7;
    private static final double WALL_DAMAGE_MARGIN = 1.5;
    private static final double WALL_DAMAGE = 0.10;
    private static final double RELIC_PICKUP_RADIUS_SQUARED = 36;

    private final List<ItemDisplay> displays = new ArrayList<>();

    private Game game;
    private List<Location> route = List.of();
    private double routeLength;
    private double wallDistance = START_DISTANCE;
    private int ticks;
    private boolean started;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.game.registerEvents(this);
        this.route = game.getMarkers(DunestarRouteMarker.class)
                .stream()
                .sorted(Comparator.comparingInt(DunestarRouteMarker::getRouteIndex))
                .map(marker -> marker.getLocation().clone())
                .toList();
        if (route.size() != 4) {
            throw new IllegalStateException("Plains of Dunestar requires exactly four route markers");
        }
        this.routeLength = calculateRouteLength();
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                if (!started) {
                    return;
                }
                ticks++;
                wallDistance = Math.min(routeLength, wallDistance + SPEED_PER_TICK);
                if (ticks % DISPLAY_UPDATE_INTERVAL == 0) {
                    updateDisplays();
                }
                if (ticks % DAMAGE_INTERVAL == 0) {
                    damagePlayersBehindWall();
                }
            }
        }.runTaskTimer(0, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRelicPickup(EntityPickupItemEvent event) {
        if (started || !(event.getEntity() instanceof Player player)) {
            return;
        }
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (!(warlordsEntity instanceof WarlordsPlayer) || warlordsEntity.getGame() != game) {
            return;
        }
        Item item = event.getItem();
        if (item.getItemStack().getType() != Material.HEART_OF_THE_SEA) {
            return;
        }
        Location start = route.getFirst();
        if (item.getWorld() != start.getWorld() || item.getLocation().distanceSquared(start) > RELIC_PICKUP_RADIUS_SQUARED) {
            return;
        }

        new GameRunnable(game) {
            @Override
            public void run() {
                if (!started && !item.isValid()) {
                    startWall();
                }
            }
        }.runTaskLater(1);
    }

    @EventHandler
    public void onGameEnd(WarlordsGameTriggerWinEvent event) {
        if (event.getGame() == game) {
            removeWall();
        }
    }

    private void startWall() {
        started = true;
        wallDistance = START_DISTANCE;
        ticks = 0;
        spawnDisplays();
        game.forEachOnlinePlayer((player, team) -> {
            player.sendMessage(Component.text("A Dunestar storm wall is advancing behind you. Keep moving!", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 0.7f);
        });
    }

    private void spawnDisplays() {
        removeDisplays();
        RoutePoint routePoint = getRoutePoint(wallDistance);
        for (int i = 0; i < PANEL_COUNT; i++) {
            final int panelIndex = i;
            ItemDisplay display = routePoint.location().getWorld().spawn(routePoint.location(), ItemDisplay.class, itemDisplay -> {
                itemDisplay.setItemStack(new ItemStack(panelIndex % 2 == 0 ? Material.BROWN_STAINED_GLASS : Material.ORANGE_STAINED_GLASS));
                itemDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
                itemDisplay.setBillboard(Display.Billboard.FIXED);
                itemDisplay.setTransformation(new Transformation(
                        new Vector3f(),
                        new Quaternionf(),
                        new Vector3f((float) (WALL_WIDTH / PANEL_COUNT), (float) WALL_HEIGHT, (float) WALL_THICKNESS),
                        new Quaternionf()
                ));
                itemDisplay.setInterpolationDuration(DISPLAY_UPDATE_INTERVAL);
                itemDisplay.setTeleportDuration(DISPLAY_UPDATE_INTERVAL);
                itemDisplay.setViewRange(2f);
                itemDisplay.setShadowRadius(0);
                itemDisplay.setShadowStrength(0);
                itemDisplay.setPersistent(false);
                itemDisplay.setGravity(false);
                itemDisplay.setInvulnerable(true);
            });
            displays.add(display);
        }
        updateDisplays();
    }

    private void updateDisplays() {
        if (displays.isEmpty()) {
            return;
        }
        RoutePoint routePoint = getRoutePoint(wallDistance);
        Vector direction = routePoint.direction().clone();
        Vector horizontalDirection = new Vector(direction.getX(), 0, direction.getZ()).normalize();
        Vector perpendicular = new Vector(-horizontalDirection.getZ(), 0, horizontalDirection.getX());
        float yaw = (float) Math.toDegrees(Math.atan2(-horizontalDirection.getX(), horizontalDirection.getZ()));
        double panelWidth = WALL_WIDTH / PANEL_COUNT;

        for (int i = 0; i < displays.size(); i++) {
            ItemDisplay display = displays.get(i);
            if (!display.isValid()) {
                continue;
            }
            double offset = (i - (PANEL_COUNT - 1) / 2.0) * panelWidth;
            Location location = routePoint.location().clone()
                    .add(perpendicular.clone().multiply(offset))
                    .add(0, WALL_HEIGHT / 2.0, 0);
            location.setYaw(yaw);
            location.setPitch(0);
            display.teleport(location);
        }
    }

    private void damagePlayersBehindWall() {
        double maxLateralDistance = WALL_WIDTH / 2.0 + 2;
        double maxLateralDistanceSquared = maxLateralDistance * maxLateralDistance;
        game.warlordsPlayers().forEach(player -> {
            if (player.isDead() || !player.isOnline()) {
                return;
            }
            RouteProjection projection = projectOntoRoute(player.getLocation());
            if (projection.lateralDistanceSquared() > maxLateralDistanceSquared
                    || projection.distanceAlongRoute() > wallDistance + WALL_DAMAGE_MARGIN) {
                return;
            }
            player.addInstance(InstanceBuilder
                    .damage()
                    .cause("Dunestar Storm Wall")
                    .source(player)
                    .value(player.getMaxHealth() * (float) WALL_DAMAGE)
                    .flags(InstanceFlags.TRUE_DAMAGE, InstanceFlags.IGNORE_CRIT_MODIFIERS)
            );
        });
    }

    private double calculateRouteLength() {
        double length = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            length += horizontalDistance(route.get(i), route.get(i + 1));
        }
        return length;
    }

    private RoutePoint getRoutePoint(double distanceAlongRoute) {
        if (distanceAlongRoute <= 0) {
            Location start = route.getFirst();
            Location next = route.get(1);
            double segmentLength = horizontalDistance(start, next);
            double progress = distanceAlongRoute / segmentLength;
            return interpolate(start, next, progress);
        }

        double remaining = distanceAlongRoute;
        for (int i = 0; i < route.size() - 1; i++) {
            Location start = route.get(i);
            Location end = route.get(i + 1);
            double segmentLength = horizontalDistance(start, end);
            if (remaining <= segmentLength) {
                return interpolate(start, end, remaining / segmentLength);
            }
            remaining -= segmentLength;
        }
        return interpolate(route.get(route.size() - 2), route.getLast(), 1);
    }

    private RoutePoint interpolate(Location start, Location end, double progress) {
        Vector direction = end.toVector().subtract(start.toVector());
        Location location = start.clone().add(direction.clone().multiply(progress));
        return new RoutePoint(location, direction);
    }

    private RouteProjection projectOntoRoute(Location location) {
        if (location.getWorld() != route.getFirst().getWorld()) {
            return new RouteProjection(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        double accumulatedDistance = 0;
        double bestRouteDistance = 0;
        double bestLateralDistanceSquared = Double.MAX_VALUE;

        for (int i = 0; i < route.size() - 1; i++) {
            Location start = route.get(i);
            Location end = route.get(i + 1);
            double dx = end.getX() - start.getX();
            double dz = end.getZ() - start.getZ();
            double segmentLengthSquared = dx * dx + dz * dz;
            double segmentLength = Math.sqrt(segmentLengthSquared);
            double toPointX = location.getX() - start.getX();
            double toPointZ = location.getZ() - start.getZ();
            double progress = segmentLengthSquared == 0
                    ? 0
                    : Math.max(0, Math.min(1, (toPointX * dx + toPointZ * dz) / segmentLengthSquared));
            double closestX = start.getX() + dx * progress;
            double closestZ = start.getZ() + dz * progress;
            double lateralX = location.getX() - closestX;
            double lateralZ = location.getZ() - closestZ;
            double lateralDistanceSquared = lateralX * lateralX + lateralZ * lateralZ;

            if (lateralDistanceSquared < bestLateralDistanceSquared) {
                bestLateralDistanceSquared = lateralDistanceSquared;
                bestRouteDistance = accumulatedDistance + segmentLength * progress;
            }
            accumulatedDistance += segmentLength;
        }

        return new RouteProjection(bestRouteDistance, bestLateralDistanceSquared);
    }

    private double horizontalDistance(Location first, Location second) {
        return Math.hypot(second.getX() - first.getX(), second.getZ() - first.getZ());
    }

    private void removeWall() {
        started = false;
        removeDisplays();
    }

    private void removeDisplays() {
        for (ItemDisplay display : displays) {
            if (display.isValid()) {
                display.remove();
            }
        }
        displays.clear();
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        removeWall();
    }

    private record RoutePoint(Location location, Vector direction) {
    }

    private record RouteProjection(double distanceAlongRoute, double lateralDistanceSquared) {
    }
}

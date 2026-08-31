package com.ebicep.warlords.effects;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FallingBlockDebrisEffect {

    private static final double GRAVITY = -0.075;
    private static final double GRAVITY_HALF = GRAVITY / 2;
    private static final Transformation TRANSFORMATION = new Transformation(
            new Vector3f(-0.5f, 0f, -0.5f),
            new AxisAngle4f(),
            new Vector3f(1, 1, 1),
            new AxisAngle4f()
    );
    private static final Map<Material, BlockData> CACHED_BLOCK_DATA = new HashMap<>();

    private static final List<DebrisEntry> ACTIVE = new ArrayList<>();
    private static BukkitTask tickerTask;

    public static BlockData getBlockData(Material material) {
        return CACHED_BLOCK_DATA.computeIfAbsent(material, Material::createBlockData);
    }

    public static void spawn(Location spawnLocation, Material material, double initialVelocityY, double removeBelowY) {
        BlockData blockData = getBlockData(material);
        double startY = spawnLocation.getY();
        double removeY = startY - removeBelowY;

        spawnLocation.setPitch(0);

        BlockDisplay display = spawnLocation.getWorld().spawn(
                spawnLocation,
                BlockDisplay.class,
                false,
                blockDisplay -> {
                    blockDisplay.setBlock(blockData);
                    blockDisplay.setBrightness(EntitiesUtils.MAX_BRIGHTNESS);
                    blockDisplay.setTeleportDuration(3);
                    blockDisplay.setTransformation(TRANSFORMATION);
                }
        );

        ACTIVE.add(new DebrisEntry(display, startY, removeY, initialVelocityY));
        ensureTickerRunning();
    }

    private static void ensureTickerRunning() {
        if (tickerTask != null && !tickerTask.isCancelled()) {
            return;
        }
        tickerTask = new BukkitRunnable() {

            final Location cachedLocation = new Location(null, 0, 0, 0);

            @Override
            public void run() {
                Iterator<DebrisEntry> it = ACTIVE.iterator();
                while (it.hasNext()) {
                    DebrisEntry entry = it.next();
                    if (!entry.display.isValid()) {
                        it.remove();
                        continue;
                    }
                    int t = entry.ticksLived++;
                    double y = entry.startY + (entry.initialVelocityY * t) + (GRAVITY_HALF * t * t);
                    if (y < entry.removeY) {
                        entry.display.remove();
                        it.remove();
                        continue;
                    }
                    cachedLocation.setWorld(entry.display.getWorld());
                    cachedLocation.set(entry.display.getX(), y, entry.display.getZ());
                    cachedLocation.setRotation(entry.display.getYaw(), entry.display.getPitch());
                    entry.display.teleport(cachedLocation);
                }
                if (ACTIVE.isEmpty()) {
                    this.cancel();
                    tickerTask = null;
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 1, 1);
    }

    private static final class DebrisEntry {

        private final BlockDisplay display;
        private final double startY;
        private final double removeY;
        private final double initialVelocityY;
        private int ticksLived;

        private DebrisEntry(BlockDisplay display, double startY, double removeY, double initialVelocityY) {
            this.display = display;
            this.startY = startY;
            this.removeY = removeY;
            this.initialVelocityY = initialVelocityY;
        }
    }

}

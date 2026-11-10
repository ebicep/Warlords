package com.ebicep.warlords.effects;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FallingBlockWaveEffect {

    private static final double GRAVITY = -0.075;
    private static final double GRAVITY_HALF = GRAVITY / 2;
    private static final double INITIAL_VELOCITY = 0.1;
    private static final double DENSITY = 0.8; // decrease for less blocks
    private static final Transformation TRANSFORMATION = new Transformation(
            new Vector3f(),
            new AxisAngle4f(),
            new Vector3f(1.5f, 1.2f, 1.5f), // scale of block to give illusion of more blocks
            new AxisAngle4f()
    );
    public static void create(Location center, double range, int duration, Material material) {
        BlockData blockData = FallingBlockDebrisEffect.getBlockData(material);
        int entityCount = 0;
        List<List<Location>> fallingBlockLocations = new LinkedList<>();
        for (int i = 0; i < range; i++) {
            List<Location> locations = LocationUtils.getCircle(center, i, (i * ((int) (Math.PI * 2))));
            fallingBlockLocations.add(locations);
            entityCount += locations.size();
        }
        List<BlockDisplay> entities = new ArrayList<>(entityCount);
        new BukkitRunnable() {

            final ThreadLocalRandom random = ThreadLocalRandom.current();
            final double centerY = center.getY();
            final Location cachedLocation = center.clone();

            @Override
            public void run() {
                if (!fallingBlockLocations.isEmpty()) {
                    List<Location> fallingBlockLocation = fallingBlockLocations.removeFirst();
                    double chance = DENSITY / Math.sqrt(fallingBlockLocation.size());
                    for (Location location : fallingBlockLocation) {
                        if (!(random.nextDouble() < chance)) {
                            continue;
                        }
                        if (location.getBlock().getType().isOccluding()) {
                            continue;
                        }
                        entities.add(center.getWorld().spawn(
                                location,
                                BlockDisplay.class,
                                false,
                                blockDisplay -> {
                                    blockDisplay.setBlock(blockData);
                                    blockDisplay.setBrightness(EntitiesUtils.MAX_BRIGHTNESS);
                                    blockDisplay.setTeleportDuration(3);
                                    blockDisplay.setTransformation(TRANSFORMATION);
                                }
                        ));
                    }
                }

                Iterator<BlockDisplay> it = entities.iterator();
                while (it.hasNext()) {
                    BlockDisplay entity = it.next();
                    int t = entity.getTicksLived();
                    cachedLocation.set(
                            entity.getX(),
                            centerY + (INITIAL_VELOCITY * t) + (GRAVITY_HALF * t * t),
                            entity.getZ()
                    );
                    cachedLocation.setRotation(
                            entity.getYaw(),
                            entity.getPitch()
                    );
                    entity.teleport(cachedLocation);

                    if (t > duration) {
                        entity.remove();
                        it.remove();
                    }
                }

                if (entities.isEmpty() && fallingBlockLocations.isEmpty()) {
                    this.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 1, 1);
    }

}

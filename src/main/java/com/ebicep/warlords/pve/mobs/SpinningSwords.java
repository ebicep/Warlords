package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpinningSwords {

    private final ItemDisplay sword;
    private final Location center;
    private final WarlordsEntity entity;
    private final double radius;
    private final double height;
    private final double sweepDeg; // total swing arc
    private final int duration;    // ticks for one half-sweep
    private boolean forward = true;
    private int tick = 0;
    private int taskId = -1;

    public SpinningSwords(Location center, WarlordsEntity entity,  double radius, double height, double sweepDeg, int duration) {
        this.center = center.clone();
        this.entity = entity;
        this.radius = radius;
        this.height = height;
        this.sweepDeg = sweepDeg;
        this.duration = duration;

        World world = center.getWorld();
        sword = world.spawn(center.clone().add(0, height, 0), ItemDisplay.class, disp -> {
            disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
            disp.setBillboard(Display.Billboard.FIXED);
            disp.setInterpolationDuration(0);
            disp.setTransformation(new Transformation(
                    new Vector3f(0,0,0),
                    new Quaternionf(),
                    new Vector3f(40f,40f,40f),
                    new Quaternionf()
            ));
        });

        start();
    }

    private void start() {
        new GameRunnable(entity.getGame()) {
            @Override
            public void run() {
                tick++;

                // progress from 0 -> 1 across 'duration'
                double progress = tick / (double) duration;
                if (progress > 1.0) {
                    tick = 0;
                    progress = 0;
                    forward = !forward; // reverse sweep
                }

                // angle offset within arc
                double angleDeg = (forward ? progress : (1 - progress)) * sweepDeg - sweepDeg / 2.0;
                double rad = Math.toRadians(angleDeg);

                double x = center.getX() + radius * Math.cos(rad);
                double z = center.getZ() + radius * Math.sin(rad);
                Location pos = new Location(center.getWorld(), x, center.getY() + height, z);
                sword.teleport(pos);

                // lay the sword flat once (constant)
                Quaternionf flat = new Quaternionf().rotateX((float) Math.toRadians(90));

                Quaternionf outwardYaw = new Quaternionf().rotateY((float) rad);

                sword.setTransformation(new Transformation(
                        new Vector3f(0, 0, 0),
                        outwardYaw,
                        sword.getTransformation().getScale(),
                        flat
                ));
            }
        }.runTaskTimer(0, 0);
    }

    public void remove() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        sword.remove();
    }
}

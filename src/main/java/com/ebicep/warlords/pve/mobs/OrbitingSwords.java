package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public final class OrbitingSwords {

    private final Location loc;
    private final List<ItemDisplay> swords = new ArrayList<>();
    private final WarlordsEntity boss;
    private double radius;
    private final double height;
    private int swordCount;
    private float speedDegPerTick;
    private float baseAngleDeg = 0f;
    private int taskId = -1;

    public OrbitingSwords(Location loc, WarlordsEntity boss, double radius, double height, int swordCount, float speedDegPerTick) {
        this.loc = loc;
        this.boss = boss;
        this.radius = radius;
        this.height = height;
        this.swordCount = swordCount;
        this.speedDegPerTick = speedDegPerTick;

        World w = loc.getWorld();
        for (int i = 0; i < swordCount; i++) {
            float offset = i * (360f / swordCount); // even spacing
            ItemDisplay d = w.spawn(loc.add(0, height, 0), ItemDisplay.class, disp -> {
                disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
                disp.setBillboard(Display.Billboard.FIXED);
                disp.setInterpolationDuration(0);
                disp.setTransformation(new Transformation(
                        new Vector3f(0,0,0),
                        new Quaternionf(),
                        new Vector3f(3.5f,3.5f,3.5f), // scale up if needed
                        new Quaternionf()
                ));
            });

            d.setMetadata("angleOffset", new FixedMetadataValue(Warlords.getInstance(), offset));
            swords.add(d);
        }

        // start scheduler
        new GameRunnable(boss.getGame()) {
            @Override
            public void run() {
                if (boss.isDead()) { remove(); return; }

                baseAngleDeg = (baseAngleDeg + speedDegPerTick) % 360f;
                Location center = boss.getLocation().add(0, height, 0);

                for (ItemDisplay d : swords) {
                    float off = d.getMetadata("angleOffset").getFirst().asFloat();
                    double aRad = Math.toRadians(baseAngleDeg + off);

                    double x = center.getX() + radius * Math.cos(aRad);
                    double z = center.getZ() + radius * Math.sin(aRad);
                    d.teleport(new Location(center.getWorld(), x, center.getY(), z));

                    float yawRad = (float) Math.toRadians((baseAngleDeg + off));

                    Quaternionf faceTangent = new Quaternionf().rotateY(yawRad);

                    // Put spin in leftRotation (tangent), keep flat as rightRotation
                    d.setTransformation(new Transformation(
                            new Vector3f(0, 0, 0),
                            faceTangent,
                            d.getTransformation().getScale(),
                            new Quaternionf()
                    ));
                }
            }
        }.runTaskTimer(0, 0);
    }

    public void remove() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        swords.forEach(e -> { if (!e.isDead()) e.remove(); });
        swords.clear();
    }

    public float getSpeedDegPerTick() {
        return speedDegPerTick;
    }

    public double getRadius() {
        return radius;
    }

    public void setSpeedDegPerTick(float speedDegPerTick) {
        this.speedDegPerTick = speedDegPerTick;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}

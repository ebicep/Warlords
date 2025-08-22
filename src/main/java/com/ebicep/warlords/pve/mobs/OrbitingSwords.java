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
    private final WarlordsEntity boss;
    private final List<ItemDisplay> swords = new ArrayList<>();
    private final double radius;
    private final double height;
    private final float speedDegPerTick;
    private float baseAngleDeg = 0f;
    private int taskId = -1;

    public OrbitingSwords(WarlordsEntity boss, double radius, double height, float speedDegPerTick) {
        this.boss = boss;
        this.radius = radius;
        this.height = height;
        this.speedDegPerTick = speedDegPerTick;

        World w = boss.getWorld();
        float[] offsets = {0f, 120f, 240f}; // 3 swords spaced evenly
        for (float off : offsets) {
            ItemDisplay d = w.spawn(boss.getLocation().add(0, height, 0), ItemDisplay.class, disp -> {
                disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
                disp.setBillboard(Display.Billboard.FIXED);
                disp.setInterpolationDuration(0); // we’re teleporting per tick
                disp.setTransformation(new Transformation(
                        new Vector3f(0, 0, 0),
                        new Quaternionf(),                // animated/left
                        new Vector3f(3f, 3f, 3f),      // scale if you like
                        new Quaternionf()                 // base/right
                ));
                disp.setPersistent(true);
            });
            // store angle offset in entity’s persistent data if you prefer; here we attach as metadata
            d.setMetadata("angleOffset", new FixedMetadataValue(Warlords.getInstance(), off));
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

                    // Orientation: face the tangent of the orbit and lie flat
                    // tangent yaw = angle + 90°
                    float yawRad = (float) Math.toRadians((baseAngleDeg + off));

                    Quaternionf faceTangent = new Quaternionf().rotateY(yawRad);
                    Quaternionf flat = new Quaternionf().rotateX((float) Math.toRadians(90)); // lay sword horizontal

                    // Put spin in leftRotation (tangent), keep flat as rightRotation
                    d.setTransformation(new Transformation(
                            new Vector3f(0, 0, 0),
                            faceTangent,
                            d.getTransformation().getScale(),
                            flat
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
}

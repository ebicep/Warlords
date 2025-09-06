package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.game.option.pve.PveOption;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OrbitingSwordsManager {

    private final Supplier<Location> centerSupplier;
    private final double radius, height;
    private final float orbitSpeedDegPerTick;
    private final Vector3f scale;
    private final PveOption option;
    private final WarlordsEntity entity;

    private final List<ItemDisplay> swords = new ArrayList<>();
    private final List<Float> angleOffsets = new ArrayList<>();

    private GameRunnable orbitTask;
    private int ticks = 0;

    public OrbitingSwordsManager(
            Supplier<Location> centerSupplier,
            double radius,
            double height,
            float orbitSpeedDegPerTick,
            float scaleFactor, PveOption option,
            WarlordsEntity entity
    ) {
        this.centerSupplier = centerSupplier;
        this.radius = radius;
        this.height = height;
        this.orbitSpeedDegPerTick = orbitSpeedDegPerTick;
        this.scale = new Vector3f(scaleFactor, scaleFactor, scaleFactor);
        this.option = option;
        this.entity = entity;
    }

    public void start() {
        if (orbitTask != null) return;
        orbitTask = new GameRunnable(option.getGame()) {
            @Override public void run() { tickOrbit(); }
        };
        orbitTask.runTaskTimer(0, 1); // delay=0, period=1 tick
    }

    public void stop() {
        if (orbitTask != null) {
            orbitTask.cancel();
            orbitTask = null;
        }
        for (ItemDisplay d : swords) if (d != null && !d.isDead()) d.remove();
        swords.clear();
        angleOffsets.clear();
    }

    public void spawnSwords(int count) {
        removeAllSwordsImmediate();
        Location center = centerSafe();
        World w = entity.getWorld();
        for (int i = 0; i < count; i++) {
            float offset = i * (360f / count);
            ItemDisplay d = w.spawn(center.clone().add(0, height, 0), ItemDisplay.class, disp -> {
                disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
                disp.setBillboard(Display.Billboard.FIXED);
                disp.setInterpolationDuration(0);
                disp.setTransformation(new Transformation(
                        new Vector3f(0,0,0),
                        new Quaternionf(),              // leftRotation (dynamic yaw)
                        new Vector3f(scale),            // scale
                        new Quaternionf()
                ));
                disp.setPersistent(true);
            });
            swords.add(d);
            angleOffsets.add(offset);
        }
    }

    public void removeNextSword() {
        if (swords.isEmpty()) return;
        int index = swords.size() - 1; // or pick another removal strategy
        ItemDisplay d = swords.remove(index);
        angleOffsets.remove(index);
        if (d != null && !d.isDead()) animateShrinkAndRemove(d, 10);
    }

    public void setRemaining(int remaining) {
        while (swords.size() > remaining) removeNextSword();
    }

    /* ---------------- internals ---------------- */

    private void tickOrbit() {
        Location center = centerSafe();
        ticks++;
        float baseAngleDeg = (ticks * orbitSpeedDegPerTick) % 360f;

        for (int i = 0; i < swords.size(); i++) {
            ItemDisplay d = swords.get(i);
            if (d == null || d.isDead()) continue;

            float off = angleOffsets.get(i);
            double aRad = Math.toRadians(baseAngleDeg + off);

            double x = center.getX() + radius * Math.cos(aRad);
            double z = center.getZ() + radius * Math.sin(aRad);
            d.teleport(new Location(entity.getWorld(), x, center.getY() + height, z));

            Quaternionf outwardYaw = new Quaternionf().rotateY((float) aRad);
            Transformation cur = d.getTransformation();
            d.setTransformation(new Transformation(
                    new Vector3f(0,0,0),
                    outwardYaw,
                    new Vector3f(cur.getScale()),
                    cur.getRightRotation()
            ));
        }
    }

    private void animateShrinkAndRemove(ItemDisplay d, int ticksShrink) {
        final Vector3f start = d.getTransformation().getScale();
        new GameRunnable(option.getGame()) {
            int t = 0;
            @Override
            public void run() {
                t++;
                float p = Math.min(1f, t / (float) ticksShrink);
                float s = 1f - p;
                d.setTransformation(new Transformation(
                        new Vector3f(0,0,0),
                        d.getTransformation().getLeftRotation(),
                        new Vector3f(start).mul(s),
                        d.getTransformation().getRightRotation()
                ));
                if (p >= 1f) {
                    if (!d.isDead()) d.remove();
                    cancel();
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void removeAllSwordsImmediate() {
        for (ItemDisplay d : swords) if (d != null && !d.isDead()) d.remove();
        swords.clear();
        angleOffsets.clear();
    }

    private Location centerSafe() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0) : c.clone();
    }
}
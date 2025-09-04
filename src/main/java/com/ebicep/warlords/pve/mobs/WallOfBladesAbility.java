package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class WallOfBladesAbility {

    private final WarlordsEntity caster;
    private final WarlordsNPC warlordsNPC;
    private final Location center;
    private final double arenaRadius;
    private final float speedDegPerTick;
    private final int durationTicks;
    private final double thickness;

    private final List<ItemDisplay> swords = new ArrayList<>();
    private GameRunnable task;

    public WallOfBladesAbility(WarlordsEntity caster,
                               WarlordsNPC warlordsNPC,
                               Location center,
                               double arenaRadius,
                               float speedDegPerTick,
                               int durationTicks,
                               double thickness) {
        this.caster = caster;
        this.warlordsNPC = warlordsNPC;
        this.center = center.clone();
        this.arenaRadius = arenaRadius;
        this.speedDegPerTick = speedDegPerTick;
        this.durationTicks = durationTicks;
        this.thickness = thickness;
    }

    public void start(Game game) {
        spawnSwords();

        task = new GameRunnable(game) {
            int t = 0;
            float angle = 0f;

            @Override
            public void run() {
                t++;
                if (t > durationTicks) {
                    stop();
                    cancel();
                    return;
                }

                if (t % 10 == 0) {
                    Utils.playGlobalSound(center, Sound.BLOCK_WEEPING_VINES_BREAK, 500, 0.5f);
                }

                angle += speedDegPerTick;
                double theta = Math.toRadians(angle);
                World w = center.getWorld();

                // Wall direction vector
                double dx = Math.cos(theta);
                double dz = Math.sin(theta);

                // Teleport both swords (opposite directions)
                Location posA = center.clone().add(dx, 1.5, dz);
                Location posB = center.clone().add(-dx, 1.5, -dz);

                if (!swords.isEmpty()) {
                    swords.get(0).teleport(posA);
                    swords.get(1).teleport(posB);

                    float modelForwardFix = (float) Math.PI; // 180° flip
                    Quaternionf yawA = new Quaternionf().rotateY((float) theta + modelForwardFix);
                    Quaternionf yawB = new Quaternionf().rotateY((float) (theta + Math.PI) + modelForwardFix);

                    swords.get(0).setTransformation(new Transformation(
                            new Vector3f(0,0,0),
                            yawA,                                      // dynamic yaw (spin)
                            swords.get(0).getTransformation().getScale(),
                            new Quaternionf().rotateZ((float) Math.toRadians(+90)) // constant sideways tilt
                    ));
                    swords.get(1).setTransformation(new Transformation(
                            new Vector3f(0,0,0),
                            yawB,
                            swords.get(1).getTransformation().getScale(),
                            new Quaternionf().rotateZ((float) Math.toRadians(+90))
                    ));
                }

                // Damage check along wall diameter
                for (WarlordsEntity enemy : PlayerFilter
                        .entitiesAround(center, arenaRadius, 4, arenaRadius)
                        .aliveEnemiesOf(caster)
                ) {
                    Location p = enemy.getLocation();
                    double px = p.getX() - center.getX();
                    double pz = p.getZ() - center.getZ();

                    // projection of P onto wall axis
                    double proj = px * dx + pz * dz;
                    proj = Math.max(-arenaRadius, Math.min(arenaRadius, proj));

                    double hx = proj * dx;
                    double hz = proj * dz;

                    double distSq = (px - hx) * (px - hx) + (pz - hz) * (pz - hz);
                    if (distSq <= thickness * thickness) {
                        enemy.addInstance(InstanceBuilder
                                .damage()
                                .cause("Wall of Blades")
                                .value(300) // adjust damage value
                                .source(warlordsNPC)
                        );
                    }
                }
            }
        };
        task.runTaskTimer(0, 1);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        for (ItemDisplay d : swords) {
            if (d != null && !d.isDead()) d.remove();
        }
        swords.clear();
    }

    private void spawnSwords() {
        World w = center.getWorld();
        Quaternionf side = new Quaternionf().rotateZ((float) Math.toRadians(+90));

        ItemDisplay swordA = w.spawn(center.clone().add(0, 1.5, 0), ItemDisplay.class, disp -> {
            disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
            disp.setBillboard(Display.Billboard.FIXED);
            disp.setInterpolationDuration(0);
            disp.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(),          // leftRotation (we animate yaw here)
                    new Vector3f(40f, 40f, 40f),
                    side                        // rightRotation = sideways tilt
            ));
        });

        ItemDisplay swordB = w.spawn(center.clone().add(0, 1.5, 0), ItemDisplay.class, disp -> {
            disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
            disp.setBillboard(Display.Billboard.FIXED);
            disp.setInterpolationDuration(0);
            disp.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(),          // leftRotation (we animate yaw here)
                    new Vector3f(40f, 40f, 40f),
                    side                        // rightRotation = sideways tilt
            ));
        });

        swords.add(swordA);
        swords.add(swordB);
    }
}

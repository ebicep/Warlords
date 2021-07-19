package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class WaterBreath extends AbstractAbility {

    public WaterBreath() {
        super("Water Breath", 556.5f, 753.9f, 12.53f, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Breathe water in a cone in front of you,\n" +
                "§7Knocking back enemies and restoring §a" + minDamageHeal + "\n" +
                "§7- §a" + maxDamageHeal + " §7health to yourself and all\n" +
                "§7allies hit.";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        Vector viewDirection = player.getLocation().getDirection();
        PlayerFilter.entitiesAround(player, 8.0, 4.5, 8.0)
                .forEach(target -> {
                    Vector direction = target.getLocation().subtract(player.getLocation()).toVector().normalize();
                    if (viewDirection.dot(direction) > .68) {
                        if (name.contains("Water")) {
                            if (wp.isTeammateAlive(target)) {
                                target.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            } else {
                                Location eye = player.getEyeLocation();
                                eye.setY(eye.getY() + .7);

                                final Location loc = target.getLocation();
                                final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-0.85).setY(0.3);

                                target.setVelocity(v);
                            }
                        }
                    }
                });
        wp.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.waterbreath.activation", 2, 1);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                this.playEffect();
                this.playEffect();
            }

            int animationTimer = 0;
            final Matrix4d center = new Matrix4d(player.getEyeLocation());

            public void playEffect() {

                if (animationTimer > 12) {
                    this.cancel();
                }

                ParticleEffect.HEART.display(0.2f, 0.2f, 0.2f, 1F, 1, center.translateVector(player.getWorld(), 0, 0, 0), 500);

                for (int i = 0; i < 4; i++) {
                    double angle = Math.toRadians(i * 90) + animationTimer * 0.15;
                    double width = animationTimer * 0.3;
                    ParticleEffect.DRIP_WATER.display(0, 0, 0, 0, 1,
                            center.translateVector(player.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width), 500);
                    ParticleEffect.ENCHANTMENT_TABLE.display(0, 0, 0, 0, 1,
                            center.translateVector(player.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width), 500);
                    ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0, 1,
                            center.translateVector(player.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width), 500);
                }

                animationTimer++;
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 1);
    }
}

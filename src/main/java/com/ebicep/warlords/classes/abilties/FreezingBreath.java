package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class FreezingBreath extends AbstractAbility {

    public FreezingBreath() {
        super("Freezing Breath", -422, -585, 6.3f, 60, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Breathe cold air in a cone in front\n" +
                "§7of you, dealing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                "§7to all enemies hit and slowing them by\n" +
                "§e35% §7for §64 §7seconds.";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        Vector viewDirection = player.getLocation().getDirection();
        PlayerFilter.entitiesAround(player, 8.0, 4.5, 8.0)
                .forEach(target -> {
                    Vector direction = target.getLocation().subtract(player.getLocation()).toVector().normalize();
                    if (viewDirection.dot(direction) > .68) {
                        target.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        target.getSpeed().addSpeedModifier("Freezing Breath", -35, 4 * 20);
                    }
                });
        wp.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.freezingbreath.activation", 2, 1);
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
                    //Bukkit.broadcastMessage(String.valueOf(center));
                }

                ParticleEffect.CLOUD.display(0F, 0F, 0F, 0.6F, 5, center.translateVector(player.getWorld(), animationTimer / 2D, 0, 0), 500);

                for (int i = 0; i < 4; i++) {
                    double angle = Math.toRadians(i * 90) + animationTimer * 0.15;
                    double width = animationTimer * 0.3;
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1,
                            center.translateVector(player.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width), 500);
                }

                animationTimer++;
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 1);
    }
}

package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class WideGuard extends AbstractAbility {

    public WideGuard() {
        super("Wide Guard", 0, 0, 27, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "PLACEHOLDER";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        wp.getCooldownManager().addCooldown("Wide Guard", this.getClass(), WideGuard.class, "GUARD", 4, wp, CooldownTypes.ABILITY);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(wp.getLocation(), "mage.timewarp.teleport", 2, 2);
            player1.playSound(player.getLocation(), "warrior.intervene.impact", 2, 0.1f);
        }

        // First Particle Sphere
        Location particleLoc = wp.getLocation();
        particleLoc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i) * 5.5;
            double y = Math.cos(i) * 5.5;
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;

                particleLoc.add(x, y, z);
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(76, 168, 168), particleLoc, 500);
                particleLoc.subtract(x, y, z);
            }
        }

        // Second Particle Sphere
        wp.getGame().getGameTasks().put(

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
                        double radius = Math.sin(i) * 4;
                        double y = Math.cos(i) * 4;
                        for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
                            double x = Math.cos(a) * radius;
                            double z = Math.sin(a) * radius;

                            particleLoc.add(x, y, z);
                            ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(65, 185, 185), particleLoc, 500);
                            particleLoc.subtract(x, y, z);
                        }
                    }

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), "warrior.intervene.impact", 2, 0.2f);
                    }
                }
            }.runTaskLater(Warlords.getInstance(), 5),
            System.currentTimeMillis()
        );

        // Third Particle Sphere
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!wp.getCooldownManager().getCooldown(WideGuard.class).isEmpty()) {
                            Location particleLoc = wp.getLocation();
                            particleLoc.add(0, 1, 0);

                            ParticleEffect.ENCHANTMENT_TABLE.display(0.2F, 0F, 0.2F, 0.1F, 1, particleLoc, 500);

                            for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
                                double radius = Math.sin(i) * 3.5;
                                double y = Math.cos(i) * 3;
                                for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
                                    double x = Math.cos(a) * radius;
                                    double z = Math.sin(a) * radius;

                                    particleLoc.add(x, y, z);
                                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(184, 190, 190), particleLoc, 500);
                                    particleLoc.subtract(x, y, z);
                                }
                            }

                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(particleLoc, Sound.GLASS, 2, 2);
                            }
                        } else {
                            this.cancel();

                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(particleLoc, Sound.AMBIENCE_THUNDER, 2, 1.5f);
                            }

                            CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation(), 4);
                            circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(2));
                            circle.playEffects();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 10, 8),
                System.currentTimeMillis()
        );
    }
}

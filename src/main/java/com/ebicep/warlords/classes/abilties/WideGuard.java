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
        playSphereAnimation(player, 5.5, 68, 176, 176);

        // Second Particle Sphere
        wp.getGame().getGameTasks().put(

            new BukkitRunnable() {
                @Override
                public void run() {
                    playSphereAnimation(player, 4, 65, 185, 185);

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

                            playSphereAnimation(player, 3, 190, 190, 190);

                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(player.getLocation(), Sound.CREEPER_DEATH, 2, 2);
                            }
                        } else {
                            this.cancel();

                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 2, 1.5f);
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

    /**
     * @param player what player should the sphere be around.
     * @param sphereRadius is how big the sphere should be.
     * @param red is the RGB assigned color for the particles.
     * @param green is the RGB assigned color for the particles.
     * @param blue is the RGB assigned color for the particles.
     */
    public void playSphereAnimation(Player player, double sphereRadius, int red, int green, int blue) {
        Location particleLoc = player.getLocation();
        particleLoc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = Math.cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;

                particleLoc.add(x, y, z);
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(red, green, blue), particleLoc, 500);
                particleLoc.subtract(x, y, z);
            }
        }
    }
}

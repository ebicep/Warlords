package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class WaterBreath extends AbstractAbility {

    public WaterBreath() {
        super("Water Breath", 528, 723, 6.3f, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Breathe water in a cone in front of you,\n" +
                "§7knocking back enemies, cleansing all §ede-buffs\n" +
                "§7and restoring §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health to\n" +
                "§7yourself and all allies hit." +
                "\n\n" +
                "§7Water Breath can overheal allies for up to\n" +
                "§a10% §7of their max health as bonus health\n" +
                "§7for §6" + Utils.OVERHEAL_DURATION + " §7seconds.";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().removeDebuffCooldowns();
        wp.getSpeed().removeSlownessModifiers();
        wp.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);

        Location playerLoc = player.getLocation();
        playerLoc.setPitch(0);
        playerLoc.add(0, 1.7, 0);

        Vector viewDirection = playerLoc.getDirection();

        Location hitbox = player.getLocation();
        hitbox.setPitch(0);
        hitbox.add(hitbox.getDirection().multiply(-1));

        PlayerFilter.entitiesAroundRectangle(playerLoc, 7.5, 10, 7.5)
            .excluding(wp)
            .forEach(target -> {
                Vector direction = target.getLocation().subtract(hitbox).toVector().normalize();
                if (viewDirection.dot(direction) > .68) {
                    if (wp.isTeammateAlive(target)) {
                        target.getCooldownManager().removeDebuffCooldowns();
                        target.getSpeed().removeSlownessModifiers();
                        target.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
                        target.getCooldownManager().removeCooldown(Utils.OVERHEAL_MARKER);
                        target.getCooldownManager().addCooldown("Overheal",
                                null, Utils.OVERHEAL_MARKER, "OVERHEAL", Utils.OVERHEAL_DURATION, wp, CooldownTypes.BUFF);
                    } else {
                        final Location loc = target.getLocation();
                        final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.1).setY(0.2);

                        target.setVelocity(v);
                    }
                }
            });

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.waterbreath.activation", 2, 1);
        }

        ParticleEffect.HEART.display(0.6f, 0.6f, 0.6f, 1, 2, player.getLocation().add(0, 0.7, 0), 500);
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        this.playEffect();
                        this.playEffect();
                    }

                    int animationTimer = 0;
                    final Matrix4d center = new Matrix4d(playerLoc);

                    public void playEffect() {

                        if (animationTimer > 12) {
                            this.cancel();
                        }

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
                }.runTaskTimer(Warlords.getInstance(), 0, 1),
                System.currentTimeMillis()
        );
    }
}

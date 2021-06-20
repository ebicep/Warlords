package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Breath extends AbstractAbility {

    public Breath(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        Classes selected = Classes.getSelected(player);
        if (selected == Classes.AQUAMANCER) {
            description = "§7Breathe water in a cone in front of you,\n" +
                    "§7Knocking back enemies and restoring §a" + minDamageHeal + "\n" +
                    "§7- §a" + maxDamageHeal + " §7health to yourself and all\n" +
                    "§7allies hit.";
        } else if (selected == Classes.CRYOMANCER) {
            description = "§7Breathe cold air in a cone in front\n" +
                    "§7of you, dealing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7to all enemies hit and slowing them by\n" +
                    "§e35% §7for §64 §7seconds.";
        }
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

        if (name.contains("Water")) {
            warlordsPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        }

        Vector viewDirection = player.getLocation().getDirection();
        List<Entity> near = player.getNearbyEntities(6.0D, 3.5D, 6.0D);
        for (Entity entity : near) {
            if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                Player nearPlayer = (Player) entity;
                Vector direction = nearPlayer.getLocation().subtract(player.getLocation()).toVector().normalize();
                if (viewDirection.dot(direction) > .7) {
                    if (name.contains("Water")) {
                        if (Warlords.game.onSameTeam(warlordsPlayer, Warlords.getPlayer(nearPlayer))) {
                            Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        } else {
                            Location eye = player.getEyeLocation();
                            eye.setY(eye.getY() + .7);

                            final Location loc = entity.getLocation();
                            final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-0.85).setY(0.3);

                            entity.setVelocity(v);
                        }
                    } else if (name.contains("Freezing") && !Warlords.game.onSameTeam(warlordsPlayer, Warlords.getPlayer(nearPlayer))) {
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        Warlords.getPlayer(nearPlayer).getSpeed().changeCurrentSpeed("Freezing Breath", -35, 4 * 20);
                    }
                }
            }

        }
        warlordsPlayer.subtractEnergy(energyCost);

        if (name.contains("Water")) {
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
                        //Bukkit.broadcastMessage(String.valueOf(center));
                    }

                    ParticleEffect.HEART.display(0F, 0F, 0F, 1F, 2, center.translateVector(player.getWorld(), 0, 0, 0), 500);

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

        } else if (name.contains("Freezing")) {
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
}
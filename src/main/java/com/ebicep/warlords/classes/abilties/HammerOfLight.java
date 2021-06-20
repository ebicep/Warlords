package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;

public class HammerOfLight extends AbstractAbility {

    public HammerOfLight() {
        super("Hammer of Light", 159.85f, 216.2f, 60f + 10.47f, 30, 20, 175
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Throw down a Hammer of Light on\n" +
                "§7the ground, dealing §c118.8 §7-\n" +
                "§c158.4 §7damage every second to\n" +
                "§7nearby enemies and healing nearby\n" +
                "§7allies for §a" + Math.floor(minDamageHeal) + " §7- §a" + Math.floor(maxDamageHeal) + " §7every\n" +
                "§7second. Your Protector Strike pierces\n" +
                "§7shields and defenses of enemies standing\n" +
                "§7on top of the Hammer of Light. §7Lasts §68\n" +
                "§7seconds.";
    }

    @Override
    public void onActivate(Player player) {
        if (player.getTargetBlock((HashSet<Byte>) null, 15).getType() == Material.AIR) return;
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        DamageHealCircle damageHealCircle = new DamageHealCircle(player, player.getTargetBlock((HashSet<Byte>) null, 15).getLocation().add(1, 0, 1), 6, 8, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawnHammer();
        damageHealCircle.getLocation().add(0, 1, 0);
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.getSpec().getOrange().setCurrentCooldown(cooldown);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.hammeroflight.impact", 2, 1);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), damageHealCircle::spawn, 0, 1);

        new BukkitRunnable() {

            @Override
            public void run() {
                damageHealCircle.setDuration(damageHealCircle.getDuration() - 1);
                List<Entity> near = (List<Entity>) damageHealCircle.getLocation().getWorld().getNearbyEntities(damageHealCircle.getLocation(), 5, 4, 5);
                for (Entity entity : near) {
                    if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                        Player player = (Player) entity;
                        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
                        double distance = damageHealCircle.getLocation().distanceSquared(player.getLocation());
                        if (distance < damageHealCircle.getRadius() * damageHealCircle.getRadius()) {
                            if (Warlords.game.onSameTeam((Player) entity, damageHealCircle.getPlayer())) {
                                warlordsPlayer.addHealth(Warlords.getPlayer(damageHealCircle.getPlayer()), damageHealCircle.getName(), damageHealCircle.getMinDamage(), damageHealCircle.getMaxDamage(), damageHealCircle.getCritChance(), damageHealCircle.getCritMultiplier());
                            } else {
                                warlordsPlayer.addHealth(Warlords.getPlayer(damageHealCircle.getPlayer()), damageHealCircle.getName(), -118.8f, -158.4f, damageHealCircle.getCritChance(), damageHealCircle.getCritMultiplier());
                            }
                        }

                    }
                }
                if (damageHealCircle.getDuration() == 0) {
                    damageHealCircle.removeHammer();
                    this.cancel();
                    task.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    public static boolean standingInHammer(Player owner, Player standing) {
        if (!(Warlords.getPlayer(owner).getSpec() instanceof Protector)) return false;
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Hammer of Light - " + owner.getName())) {
                if (entity.getLocation().clone().add(0, 2, 0).distanceSquared(standing.getLocation()) < 5 * 5.25) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
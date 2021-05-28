package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;

public class HammerOfLight extends AbstractAbility {
    public HammerOfLight() {
        super("Hammer of Light", -119, -158, 60 + 11, 30, 20, 175,
                "§7Throw down a Hammer of Light on\n" +
                        "§7the ground, dealing §c119 §7-\n" +
                        "§c158 §7damage every second to\n" +
                        "§7nearby enemies and healing nearby\n" +
                        "§7allies for §a160 §7- §a216 §7every\n" +
                        "§7second. Your Protector Strike pierces\n" +
                        "§7shields and defenses of enemies standing\n" +
                        "§7on top of the Hammer of Light. §7Lasts §68\n" +
                        "§7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        DamageHealCircle damageHealCircle = new DamageHealCircle(player, player.getTargetBlock((HashSet<Byte>) null, 15).getLocation().add(1, 0, 1), 6, 8, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawnHammer();
        Warlords.getPlayer(player).subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.hammeroflight.impact", 2, 1);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                damageHealCircle.spawn();
                damageHealCircle.setDuration(damageHealCircle.getDuration() - 1);
                List<Entity> near = (List<Entity>) damageHealCircle.getLocation().getWorld().getNearbyEntities(damageHealCircle.getLocation(), 5, 4, 5);
                for (Entity entity : near) {
                    if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                        Player player = (Player) entity;
                        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
                        double distance = damageHealCircle.getLocation().distanceSquared(player.getLocation());
                        if (distance < damageHealCircle.getRadius() * damageHealCircle.getRadius()) {
                            if (Warlords.game.onSameTeam((Player) entity, damageHealCircle.getPlayer())) {
                                warlordsPlayer.addHealth(Warlords.getPlayer(damageHealCircle.getPlayer()), damageHealCircle.getName(), 160, 216, damageHealCircle.getCritChance(), damageHealCircle.getCritMultiplier());
                            } else {
                                warlordsPlayer.addHealth(Warlords.getPlayer(damageHealCircle.getPlayer()), damageHealCircle.getName(), damageHealCircle.getMinDamage(), damageHealCircle.getMaxDamage(), damageHealCircle.getCritChance(), damageHealCircle.getCritMultiplier());
                            }
                        }

                    }
                }
                if (damageHealCircle.getDuration() == 0) {
                    damageHealCircle.removeHammer();
                    this.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }
}
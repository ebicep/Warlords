package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.ActionBarStats;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;

public class HealingRain extends AbstractAbility {

    public HealingRain() {
        super("Healing Rain", 170, 230, 53, 50, 15, 200,
                "§7Conjure rain at targeted\n" +
                        "§7location that will restore §a170\n" +
                        "§7- §a230 §7health every second to\n" +
                        "§7allies. Lasts §610 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        if (player.getTargetBlock((HashSet<Byte>) null, 15).getType() == Material.AIR) return;
        DamageHealCircle damageHealCircle = new DamageHealCircle(player, player.getTargetBlock((HashSet<Byte>) null, 15).getLocation(), 7, 10, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.getLocation().add(0, 1, 0);
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "RAIN", 10));
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.getSpec().getOrange().setCurrentCooldown(cooldown);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.healingrain.impact", 2, 1);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), damageHealCircle::spawn, 0, 1);

        new BukkitRunnable() {

            @Override
            public void run() {
                damageHealCircle.setDuration(damageHealCircle.getDuration() - 1);
                List<Entity> near = (List<Entity>) damageHealCircle.getLocation().getWorld().getNearbyEntities(damageHealCircle.getLocation(), 5, 4, 5);
                near = Utils.filterOnlyTeammates(near, player);
                for (Entity entity : near) {
                    if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                        Player player = (Player) entity;
                        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
                        double distance = damageHealCircle.getLocation().distanceSquared(player.getLocation());
                        if (distance < damageHealCircle.getRadius() * damageHealCircle.getRadius()) {
                            warlordsPlayer.addHealth(Warlords.getPlayer(damageHealCircle.getPlayer()), damageHealCircle.getName(), damageHealCircle.getMinDamage(), damageHealCircle.getMaxDamage(), damageHealCircle.getCritChance(), damageHealCircle.getCritMultiplier());
                        }

                    }
                }
                if (damageHealCircle.getDuration() == 0) {
                    this.cancel();
                    task.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }
}

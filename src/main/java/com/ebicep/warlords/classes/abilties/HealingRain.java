package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.ActionBarStats;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;


public class HealingRain extends AbstractAbility {

    private int recastCooldown = 0;

    public HealingRain() {
        super("Healing Rain", 170, 230, 52.85f, 50, 15, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Conjure rain at targeted\n" +
                "location that will restore §a" + minDamageHeal + "\n" +
                "§7- §a" + maxDamageHeal + " §7health every second to\n" +
                "allies. Lasts §612 §7seconds.\n\nYou may\n" +
                "move Healing Rain to your location\n" +
                "every §63 §7seconds using your SNEAK key.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        if (player.getTargetBlock((HashSet<Byte>) null, 15).getType() == Material.AIR) return;
        DamageHealCircle damageHealCircle = new DamageHealCircle(warlordsPlayer, player.getTargetBlock((HashSet<Byte>) null, 15).getLocation(), 6, 12, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.getLocation().add(0, 1, 0);
        warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "RAIN", 12));
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.getSpec().getOrange().setCurrentCooldown(cooldown);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.healingrain.impact", 2, 1);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), damageHealCircle::spawn, 0, 1);

        BukkitTask task1 = new BukkitRunnable() {
            boolean wasSneaking = false;
            @Override
            public void run() {
                if (player.isSneaking() && !wasSneaking) {
                    if (recastCooldown != 0) {
                        player.sendMessage("§cYour recast ability is on cooldown, please wait 3 seconds!");
                    } else {
                        player.playSound(player.getLocation(), "mage.waterbreath.activation", 2, 1.5f);
                        damageHealCircle.setLocation(player.getLocation());
                        recastCooldown = 3;
                    }
                }

                wasSneaking = player.isSneaking();
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 0);

        new BukkitRunnable() {

            @Override
            public void run() {
                damageHealCircle.setDuration(damageHealCircle.getDuration() - 1);

                PlayerFilter.entitiesAround(damageHealCircle.getLocation(), 6, 4, 6)
                    .aliveTeammatesOf(warlordsPlayer)
                    .forEach((warlordsPlayer) -> {
                        double distance = damageHealCircle.getLocation().distanceSquared(player.getLocation());
                        if (distance < damageHealCircle.getRadius() * damageHealCircle.getRadius()) {
                            warlordsPlayer.addHealth(damageHealCircle.getWarlordsPlayer(), damageHealCircle.getName(), damageHealCircle.getMinDamage(), damageHealCircle.getMaxDamage(), damageHealCircle.getCritChance(), damageHealCircle.getCritMultiplier());
                        }

                    });
                if (damageHealCircle.getDuration() < 0) {
                    this.cancel();
                    task.cancel();
                    task1.cancel();
                }

                if (recastCooldown != 0) {
                    recastCooldown--;
                }

            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);

    }
}

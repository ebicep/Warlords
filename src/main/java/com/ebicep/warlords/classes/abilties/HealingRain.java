package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;


public class HealingRain extends AbstractAbility {

    private int recastCooldown = 0;

    private final int duration = 12;

    public HealingRain() {
        super("Healing Rain", 100, 125, 52.85f, 50, 25, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Conjure rain at targeted\n" +
                "location that will restore §a" + format(minDamageHeal) + "\n" +
                "§7- §a" + format(maxDamageHeal) + " §7health every 0.5 seconds\n" +
                "to allies. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "You may move Healing Rain to your location\n" +
                "every §62 §7seconds using your SNEAK key.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {

        if (player.getTargetBlock((HashSet<Byte>) null, 25).getType() == Material.AIR) return;
        DamageHealCircle healingRain = new DamageHealCircle(wp, player.getTargetBlock((HashSet<Byte>) null, 25).getLocation(), 8, duration, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        healingRain.getLocation().add(0, 1, 0);
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().addCooldown(name, HealingRain.this.getClass(), new HealingRain(), "RAIN", duration, wp, CooldownTypes.ABILITY);
        wp.getSpec().getOrange().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(healingRain.getLocation(), "mage.healingrain.impact", 2, 1);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), healingRain::spawn, 0, 1);
        wp.getGame().getGameTasks().put(task, System.currentTimeMillis());

        BukkitTask rainSneakAbility = new BukkitRunnable() {
            boolean wasSneaking = false;

            @Override
            public void run() {
                if (wp.isAlive() && player.isSneaking() && !wasSneaking) {
                    if (recastCooldown != 0) {
                        player.sendMessage(ChatColor.RED + "Your recast ability is on cooldown, please wait 2 seconds!");
                    } else {
                        player.playSound(player.getLocation(), "mage.timewarp.teleport", 2, 1.35f);
                        player.sendMessage("§7You moved your §aHealing Rain §7to your current location.");
                        healingRain.setLocation(player.getLocation());
                        recastCooldown = 2;
                    }
                }

                wasSneaking = player.isSneaking();
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 0);
        wp.getGame().getGameTasks().put(rainSneakAbility, System.currentTimeMillis());
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        PlayerFilter.entitiesAround(healingRain.getLocation(), healingRain.getRadius(), healingRain.getRadius(), healingRain.getRadius())
                            .aliveTeammatesOf(wp)
                            .forEach((teammateInRain) -> {
                                teammateInRain.healHealth(
                                        healingRain.getWarlordsPlayer(),
                                        healingRain.getName(),
                                        healingRain.getMinDamage(),
                                        healingRain.getMaxDamage(),
                                        healingRain.getCritChance(),
                                        healingRain.getCritMultiplier(),
                                        false);

                                if (teammateInRain != wp) {
                                    teammateInRain.getCooldownManager().removeCooldown(Utils.OVERHEAL_MARKER);
                                    teammateInRain.getCooldownManager().addCooldown("Overheal",
                                            null, Utils.OVERHEAL_MARKER, "OVERHEAL", Utils.OVERHEAL_DURATION, wp, CooldownTypes.BUFF);
                                }
                        });

                        if (healingRain.getDuration() < 0) {
                            this.cancel();
                            task.cancel();
                            rainSneakAbility.cancel();
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 10),
                System.currentTimeMillis()
        );

        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        healingRain.setDuration(healingRain.getDuration() - 1);

                        if (recastCooldown != 0) {
                            recastCooldown--;
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 20),
                System.currentTimeMillis()
        );

    }
}

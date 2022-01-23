package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;


public class HealingRain extends AbstractAbility {

    private int duration = 12;
    private int radius = 8;

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
                "using your SNEAK key." +
                "\n\n" +
                "§7Healing Rain can overheal allies for up to\n" +
                "§a10% §7of their max health as bonus health\n" +
                "§7for §6" + Utils.OVERHEAL_DURATION + " §7seconds.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {

        if (player.getTargetBlock((HashSet<Byte>) null, 25).getType() == Material.AIR) return false;
        DamageHealCircle hr = new DamageHealCircle(wp, player.getTargetBlock((HashSet<Byte>) null, 25).getLocation(), radius, duration, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        hr.getLocation().add(0, 1, 0);
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().addRegularCooldown(name, "RAIN", HealingRain.class, new HealingRain(), wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, duration * 20);
        wp.getSpec().getOrange().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(hr.getLocation(), "mage.healingrain.impact", 2, 1);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), hr::spawn, 0, 1);
        wp.getGame().getGameTasks().put(task, System.currentTimeMillis());

        BukkitTask rainSneakAbility = new BukkitRunnable() {
            boolean wasSneaking = false;

            @Override
            public void run() {
                if (!wp.getGame().isGameFreeze()) {
                    if (wp.isAlive() && player.isSneaking() && !wasSneaking) {
                        player.playSound(player.getLocation(), "mage.timewarp.teleport", 2, 1.35f);
                        player.sendMessage(WarlordsPlayer.RECEIVE_ARROW + "§7You moved your §aHealing Rain §7to your current location.");
                        hr.setLocation(player.getLocation());
                    }

                    wasSneaking = player.isSneaking();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 0);
        wp.getGame().getGameTasks().put(rainSneakAbility, System.currentTimeMillis());
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!wp.getGame().isGameFreeze()) {
                            PlayerFilter.entitiesAround(hr.getLocation(), hr.getRadius(), hr.getRadius(), hr.getRadius())
                                    .aliveTeammatesOf(wp)
                                    .forEach((teammateInRain) -> {
                                        teammateInRain.addHealingInstance(
                                                hr.getWarlordsPlayer(),
                                                hr.getName(),
                                                hr.getMinDamage(),
                                                hr.getMaxDamage(),
                                                hr.getCritChance(),
                                                hr.getCritMultiplier(),
                                                false,
                                                false);

                                        if (teammateInRain != wp) {
                                            teammateInRain.getCooldownManager().removeCooldown(Utils.OVERHEAL_MARKER);
                                            teammateInRain.getCooldownManager().addRegularCooldown("Overheal",
                                                    "OVERHEAL", null, Utils.OVERHEAL_MARKER, wp, CooldownTypes.BUFF, cooldownManager -> {
                                                    }, Utils.OVERHEAL_DURATION * 20);
                                            ;
                                        }
                                    });

                            if (hr.getDuration() < 0) {
                                this.cancel();
                                task.cancel();
                                rainSneakAbility.cancel();
                            }
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 10),
                System.currentTimeMillis()
        );

        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!wp.getGame().isGameFreeze()) {
                            hr.setDuration(hr.getDuration() - 1);
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 20),
                System.currentTimeMillis()
        );

        return true;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

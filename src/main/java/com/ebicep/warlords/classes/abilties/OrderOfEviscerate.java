package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class OrderOfEviscerate extends AbstractAbility {

    private final int duration = 8;

    public OrderOfEviscerate() {
        super("Order of Eviscerate", 0, 0, 60, 30, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Cloak yourself for §6" + duration + " §7seconds, making you invisible\n" +
                "§7to the enemy for the duration. However, taking fall damage\n" +
                "§7or taking any type of ability damage will end your\n" +
                "§7invisibility." +
                "\n\n" +
                "§7All your attacks against an enemy will mark them vulnerable,\n" +
                "§7increasing the damage they take by §c25% §7for §6" + duration + " §7seconds.\n" +
                "§7All attacks that hit your marked target from behind\n" +
                "§7gain a §c100% §7crit chance." +
                "\n\n" +
                "§7Successfully killing your mark will §ereset §7both your\n" +
                "§7Blinding Assault and Order of Eviscerate's cooldown\n" +
                "§7and refund the energy cost.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().removeCooldown(OrderOfEviscerate.class);
        wp.getCooldownManager().addRegularCooldown("Order of Eviscerate", "ORDER", OrderOfEviscerate.class, new OrderOfEviscerate(), wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, duration * 20);
        wp.getCooldownManager().removeCooldownByName("Cloaked");
        wp.getCooldownManager().addRegularCooldown("Cloaked", "INVIS", OrderOfEviscerate.class, new OrderOfEviscerate(), wp, CooldownTypes.BUFF, cooldownManager -> {
        }, duration * 20);
        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier("Order of Eviscerate", 40, duration * 20, "BASE");
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration * 20, 0, true, false), true);
        wp.updateArmor();
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 2, 0.7f);
        }

        wp.getGame().getGameTasks().put(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!wp.getCooldownManager().hasCooldown(OrderOfEviscerate.class)) {
                            this.cancel();
                            wp.updateArmor();
                            wp.setMarkedTarget(null);
                            player.removePotionEffect(PotionEffectType.INVISIBILITY);
                            cancelSpeed.run();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 1),
                System.currentTimeMillis()
        );

        return true;
    }
}

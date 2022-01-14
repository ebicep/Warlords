package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
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
                "§7to the enemy for the duration. However, taking fall damage,\n" +
                "§7§7dealing damage, or taking any type of ability damage will\n" +
                "§7end your invisibility." +
                "\n\n" +
                "§7All your attacks against an enemy will mark them vulnerable,\n" +
                "§7increasing the damage they take by §c25% §7for §6" + duration + " §7seconds.\n" +
                "§7All attacks that hit your marked target from behind\n" +
                "§7gain a §c100% §7crit chance. (Limited to marking 1 enemy\n" +
                "§7at the same time.)";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().removeCooldown(OrderOfEviscerate.class);
        wp.getCooldownManager().addCooldown("Order of Eviscerate", OrderOfEviscerate.class, new OrderOfEviscerate(), "ORDER", duration, wp, CooldownTypes.ABILITY);
        wp.getCooldownManager().removeCooldownByName("Cloaked");
        wp.getCooldownManager().addCooldown("Cloaked", OrderOfEviscerate.class, new OrderOfEviscerate(), "INVIS", duration, wp, CooldownTypes.BUFF);
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
                        if (wp.getCooldownManager().getCooldown(OrderOfEviscerate.class).isEmpty()) {
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
    }
}

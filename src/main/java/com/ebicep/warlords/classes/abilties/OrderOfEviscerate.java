package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

import static com.ebicep.warlords.player.WarlordsPlayer.RECEIVE_ARROW;

public class OrderOfEviscerate extends AbstractAbility {

    private int duration = 8;

    public OrderOfEviscerate() {
        super("Order of Eviscerate", 0, 0, 50, 60, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Cloak yourself for §6" + duration + " §7seconds, granting\n" +
                "§7you §e40% §7movement speed and making you §einvisible\n" +
                "§7to the enemy for the duration. However, taking fall\n" +
                "§7damage or taking any type of ability damage will end\n" +
                "§7your invisibility." +
                "\n\n" +
                "§7All your attacks against an enemy will mark them vulnerable.\n" +
                "§7Vulnerable enemies take 10% more damage from your\n" +
                "§7attacks. All attacks that hit your marked target from\n" +
                "§7behind gain a §c100% §7crit chance." +
                "\n\n" +
                "§7Successfully killing your mark will §ereset §7both your\n" +
                "§7Blinding Assault and Order of Eviscerate's cooldown\n" +
                "§7and refund the energy cost. Assisting in killing your\n" +
                "§7mark will only refund half the cooldown";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        wp.getCooldownManager().removeCooldown(OrderOfEviscerate.class);
        wp.getCooldownManager().addCooldown(new RegularCooldown<OrderOfEviscerate>(
                "Order of Eviscerate",
                "ORDER",
                OrderOfEviscerate.class,
                new OrderOfEviscerate(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20
        ) {
            @Override
            public int addCritChanceFromAttacker(WarlordsDamageHealingEvent event, int currentCritChance) {
                if (!Utils.isLineOfSightAssassin(event.getPlayer().getEntity(), event.getAttacker().getEntity())) {
                    return 100;
                }
                return currentCritChance;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsPlayer attacker = event.getAttacker();
                WarlordsPlayer victim = event.getPlayer();
                if (attacker.getMarkedTarget() != victim.getUuid()) {
                    attacker.sendMessage(RECEIVE_ARROW + ChatColor.GRAY + " You have marked §e" + victim.getName());
                }
                attacker.setMarkedTarget(victim.getUuid());
            }
        });

        wp.getCooldownManager().removeCooldownByName("Cloaked");
        wp.getCooldownManager().addRegularCooldown(
                "Cloaked",
                "INVIS",
                OrderOfEviscerate.class,
                new OrderOfEviscerate(),
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                duration * 20
        );

        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier("Order of Eviscerate", 40, duration * 20, "BASE");
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration * 20, 0, true, false), true);

        wp.updateArmor();

        Utils.playGlobalSound(player.getLocation(), Sound.GHAST_FIREBALL, 2, 0.7f);

        PlayerFilter.playingGame(wp.getGame())
                .enemiesOf(wp)
                .forEach(warlordsPlayer -> {
                    LivingEntity livingEntity = warlordsPlayer.getEntity();
                    if (livingEntity instanceof Player) {
                        ((Player) livingEntity).hidePlayer(player);
                    }
                });

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (!wp.getCooldownManager().hasCooldown(OrderOfEviscerate.class)) {
                    this.cancel();
                    wp.updateArmor();
                    wp.setMarkedTarget(null);
                    cancelSpeed.run();
                    wp.getEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
                    if (wp.getEntity() instanceof Player) {
                        for (Player player1 : wp.getWorld().getPlayers()) {
                            player1.showPlayer((Player) wp.getEntity());
                        }
                    }
                } else {
                    ParticleEffect.SMOKE_NORMAL.display(0, 0.2f, 0, 0.05f, 4, wp.getLocation(), 500);
                    Utils.playGlobalSound(wp.getLocation(), Sound.AMBIENCE_CAVE, 0.08f, 2);
                }
            }
        }.runTaskTimer(0, 1);

        return true;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

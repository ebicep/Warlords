package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.Objects;

public class OrderOfEviscerate extends AbstractAbility {

    private int duration = 8;
    private float damageThreshold = 0;
    private WarlordsPlayer markedPlayer;

    public OrderOfEviscerate() {
        super("Order of Eviscerate", 0, 0, 50, 60, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Cloak yourself for §6" + duration + " §7seconds, granting\n" +
                "§7you §e40% §7movement speed and making you §einvisible\n" +
                "§7to the enemy for the duration. However, taking up to\n" +
                "§c600 §7fall damage or any type of ability damage will\n" +
                "§7end your invisibility." +
                "\n\n" +
                "§7All your attacks against an enemy will mark them vulnerable.\n" +
                "§7Vulnerable enemies take §c20% §7more damage. Additionally,\n" +
                "§7enemies hit from behind take an additional §c10% §7more damage." +
                "\n\n" +
                "§7Successfully killing your mark will §ereset §7both your\n" +
                "§7Shadow Step and Order of Eviscerate's cooldown\n" +
                "§7and refund the energy cost. Assisting in killing your\n" +
                "§7mark will only refund half the cooldown.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        damageThreshold = 0;
        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                if (damageThreshold > 600) {
                    this.cancel();
                    OrderOfEviscerate.removeCloak(wp, false);
                }

                if (wp.isDead()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0);

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
            public void doBeforeReductionFromAttacker(WarlordsDamageHealingEvent event) {
                //mark message here so it displays before damage
                WarlordsPlayer victim = event.getPlayer();
                if (victim != wp) {
                    if (!Objects.equals(this.getCooldownObject().getMarkedPlayer(), victim)) {
                        wp.sendMessage(WarlordsPlayer.GIVE_ARROW_GREEN + ChatColor.GRAY + " You have marked §e" + victim.getName());
                    }
                    this.getCooldownObject().setMarkedPlayer(victim);
                }
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (
                    Objects.equals(this.getCooldownObject().getMarkedPlayer(), event.getPlayer()) &&
                    !Utils.isLineOfSightAssassin(event.getPlayer().getEntity(), event.getAttacker().getEntity())
                ) {
                    return currentDamageValue * 1.3f;
                } else {
                    return currentDamageValue * 1.2f;
                }
            }

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (!Objects.equals(event.getPlayer(), this.getCooldownObject().getMarkedPlayer())) {
                    return;
                }
                wp.getCooldownManager().removeCooldown(OrderOfEviscerate.class);
                wp.getCooldownManager().removeCooldownByName("Cloaked");
                if (isKiller) {
                    wp.sendMessage(WarlordsPlayer.GIVE_ARROW_GREEN + ChatColor.GRAY + " You killed your mark," + ChatColor.YELLOW + " your cooldowns have been reset" + ChatColor.GRAY + "!");
                    new GameRunnable(wp.getGame()) {

                        @Override
                        public void run() {
                            wp.getSpec().getPurple().setCurrentCooldown(0);
                            wp.getSpec().getOrange().setCurrentCooldown(0);
                            wp.updatePurpleItem();
                            wp.updateOrangeItem();
                            wp.subtractEnergy(-wp.getSpec().getOrange().getEnergyCost());
                        }
                    }.runTaskLater(2);
                } else {
                    new GameRunnable(wp.getGame()) {

                        @Override
                        public void run() {
                            wp.sendMessage(WarlordsPlayer.GIVE_ARROW_GREEN + ChatColor.GRAY + " You assisted in killing your mark," + ChatColor.YELLOW + " your cooldowns have been reduced by half" + ChatColor.GRAY + "!");

                            wp.getSpec().getPurple().setCurrentCooldown(wp.getSpec().getPurple().getCurrentCooldown() / 2);
                            wp.getSpec().getOrange().setCurrentCooldown(wp.getSpec().getOrange().getCurrentCooldown() / 2);
                            wp.updatePurpleItem();
                            wp.updateOrangeItem();
                            wp.subtractEnergy(-wp.getSpec().getOrange().getEnergyCost() / 2);
                        }
                    }.runTaskLater(2);
                }
                if (wp.getEntity() instanceof Player) {
                    ((Player) wp.getEntity()).playSound(wp.getLocation(), Sound.AMBIENCE_THUNDER, 1, 2);
                }
            }
        });

        if (!FlagHolder.isPlayerHolderFlag(wp)) {
            wp.getCooldownManager().removeCooldownByName("Cloaked");
            wp.getCooldownManager().addRegularCooldown(
                    "Cloaked",
                    "INVIS",
                    OrderOfEviscerate.class,
                    null,
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    duration * 20
            );
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration * 20, 0, true, false), true);
            wp.updateArmor();
            PlayerFilter.playingGame(wp.getGame())
                    .enemiesOf(wp)
                    .forEach(warlordsPlayer -> {
                        LivingEntity livingEntity = warlordsPlayer.getEntity();
                        if (livingEntity instanceof Player) {
                            ((Player) livingEntity).hidePlayer(player);
                        }
                    });
        }

        Runnable cancelSpeed = wp.getSpeed().addSpeedModifier("Order of Eviscerate", 40, duration * 20, "BASE");
        Utils.playGlobalSound(player.getLocation(), Sound.GHAST_FIREBALL, 1.5f, 0.7f);

        new GameRunnable(wp.getGame()) {
            int counter = 0;
            @Override
            public void run() {
                counter++;
                if (!wp.getCooldownManager().hasCooldown(OrderOfEviscerate.class)) {
                    this.cancel();
                    cancelSpeed.run();
                    removeCloak(wp, true);
                } else {
                    ParticleEffect.SMOKE_NORMAL.display(0, 0.2f, 0, 0.05f, 4, wp.getLocation(), 500);
                    if (counter % 20 == 0) {
                        ParticleEffect.FOOTSTEP.display(0, 0, 0, 1, 1, wp.getLocation().add(0, 0.1, 0), 500);
                    }
                    Utils.playGlobalSound(wp.getLocation(), Sound.AMBIENCE_CAVE, 0.4f, 2);
                }
            }
        }.runTaskTimer(0, 1);

        return true;
    }

    public static void removeCloak(WarlordsPlayer warlordsPlayer, boolean forceRemove) {
        if (warlordsPlayer.getCooldownManager().hasCooldownFromName("Cloaked") || forceRemove) {
            warlordsPlayer.getCooldownManager().removeCooldownByName("Cloaked");
            warlordsPlayer.getEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
            warlordsPlayer.updateArmor();
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public WarlordsPlayer getMarkedPlayer() {
        return markedPlayer;
    }

    public void setMarkedPlayer(WarlordsPlayer markedPlayer) {
        this.markedPlayer = markedPlayer;
    }

    public float getDamageThreshold() {
        return damageThreshold;
    }

    public void addToDamageThreshold(float damageThreshold) {
        this.damageThreshold += damageThreshold;
    }
}

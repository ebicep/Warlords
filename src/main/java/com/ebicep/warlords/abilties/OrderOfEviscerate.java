package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OrderOfEviscerate extends AbstractAbility {

    public int numberOfFullResets = 0;
    public int numberOfHalfResets = 0;
    public int numberOfBackstabs = 0;

    protected float damageDoneWithOrder = 0;
    protected int mobsKilledWithOrder = 0;

    private boolean masterUpgrade = false;
    private int duration = 8;
    private float damageThreshold = 0;
    private WarlordsEntity markedPlayer;

    public OrderOfEviscerate() {
        super("Order of Eviscerate", 0, 0, 50, 60);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Cloak yourself for §6" + duration + " §7seconds, granting you §e40% §7movement speed and making you §einvisible §7to the enemy for the " +
                "duration. However, taking up to §c600 §7fall damage or any type of ability damage will end your invisibility." +
                "\n\nAll your attacks against an enemy will mark them vulnerable. Vulnerable enemies take §c20% §7more damage. " +
                "Additionally, enemies hit from behind take an additional §c10% §7more damage." +
                "\n\nSuccessfully killing your mark will §ereset §7both your Shadow Step and Order of Eviscerate's cooldown and refund the energy cost. " +
                "Assisting in killing your mark will only refund half the cooldown.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Number of Full Resets", "" + numberOfFullResets));
        info.add(new Pair<>("Number of Half Resets", "" + numberOfHalfResets));
        info.add(new Pair<>("Number of Backstabs", "" + numberOfBackstabs));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.5f, 0.7f);
        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Order of Eviscerate", 40, duration * 20, "BASE");

        wp.getCooldownManager().removeCooldown(OrderOfEviscerate.class, false);
        OrderOfEviscerate tempOrderOfEviscerate = new OrderOfEviscerate();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Order of Eviscerate",
                "ORDER",
                OrderOfEviscerate.class,
                tempOrderOfEviscerate,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    cancelSpeed.run();
                    removeCloak(wp, true);
                    if (pveUpgrade) {
                        if (tempOrderOfEviscerate.damageDoneWithOrder >= 15000 && tempOrderOfEviscerate.mobsKilledWithOrder >= 6) {
                            ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.SERIAL_KILLER);
                        }
                    }
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    Utils.playGlobalSound(wp.getLocation(), Sound.AMBIENT_CAVE, 0.4f, 2);
                    ParticleEffect.SMOKE_NORMAL.display(0, 0.2f, 0, 0.05f, 4, wp.getLocation(), 500);
                    if (ticksElapsed % 10 == 0) {
                        ParticleEffect.FOOTSTEP.display(0, 0, 0, 1, 1, wp.getLocation().add(0, 0.1, 0), 500);
                    }
                })
        ) {

            @Override
            public void doBeforeReductionFromAttacker(WarlordsDamageHealingEvent event) {
                //mark message here so it displays before damage
                WarlordsEntity victim = event.getPlayer();
                if (victim != wp) {
                    if (!Objects.equals(tempOrderOfEviscerate.getMarkedPlayer(), victim)) {
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + ChatColor.GRAY + " You have marked §e" + victim.getName());
                    }
                    tempOrderOfEviscerate.setMarkedPlayer(victim);
                }
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (
                        Objects.equals(tempOrderOfEviscerate.getMarkedPlayer(), event.getPlayer()) &&
                                !Utils.isLineOfSightAssassin(event.getPlayer().getEntity(), event.getAttacker().getEntity())
                ) {
                    numberOfBackstabs++;
                    return currentDamageValue * (pveUpgrade ? 2 : 1.3f);
                } else {
                    return currentDamageValue * 1.2f;
                }
            }

            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                tempOrderOfEviscerate.addAndCheckDamageThreshold(currentDamageValue, wp);
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                tempOrderOfEviscerate.damageDoneWithOrder += currentDamageValue;
            }

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (!Objects.equals(event.getPlayer(), tempOrderOfEviscerate.getMarkedPlayer())) {
                    return;
                }
                if (!pveUpgrade) {
                    this.setTicksLeft(0);
                } else {
                    removeCloak(wp, false);
                }
                if (isKiller) {
                    numberOfFullResets++;

                    if (pveUpgrade) {
                        tempOrderOfEviscerate.mobsKilledWithOrder++;
                    }

                    new GameRunnable(wp.getGame()) {
                        @Override
                        public void run() {
                            if (pveUpgrade) {
                                int reduction = masterUpgrade ? 12 : 8;
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                                        ChatColor.GRAY + " You killed your mark," +
                                        ChatColor.YELLOW + " your ultimate cooldown has been reduced by " + reduction + " seconds" +
                                        ChatColor.GRAY + "!"
                                );
                                wp.subtractPurpleCooldown(2);
                                wp.subtractOrangeCooldown(reduction);
                            } else {
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                                        ChatColor.GRAY + " You killed your mark," +
                                        ChatColor.YELLOW + " your cooldowns have been reset" +
                                        ChatColor.GRAY + "!"
                                );
                                wp.setPurpleCurrentCooldown(0);
                                wp.setOrangeCurrentCooldown(0);
                                wp.addEnergy(wp, name, energyCost);
                            }
                        }
                    }.runTaskLater(2);
                } else {
                    numberOfHalfResets++;

                    new GameRunnable(wp.getGame()) {
                        @Override
                        public void run() {
                            if (pveUpgrade) {
                                int reduction = masterUpgrade ? 6 : 4;
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                                        ChatColor.GRAY + " You assisted in killing your mark," +
                                        ChatColor.YELLOW + " your ultimate cooldown has been reduced by " + reduction + " seconds" +
                                        ChatColor.GRAY + "!"
                                );
                                //wp.getPurpleAbility().subtractCooldown(reduction);
                                wp.subtractOrangeCooldown(reduction);
                            } else {
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                                        ChatColor.GRAY + " You assisted in killing your mark," +
                                        ChatColor.YELLOW + " your cooldowns have been reduced by half" +
                                        ChatColor.GRAY + "!"
                                );
                                wp.setPurpleCurrentCooldown(wp.getPurpleAbility().getCurrentCooldown() / 2);
                                wp.setOrangeCurrentCooldown(wp.getOrangeAbility().getCurrentCooldown() / 2);
                                wp.addEnergy(wp, name, energyCost / 2f);
                            }
                        }
                    }.runTaskLater(2);
                }

                wp.playSound(wp.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 2);
            }
        });

        if (!FlagHolder.isPlayerHolderFlag(wp)) {
            giveCloak(wp, duration);
        }

        return true;
    }

    public static void removeCloak(WarlordsEntity warlordsPlayer, boolean forceRemove) {
        if (warlordsPlayer.getCooldownManager().hasCooldownFromName("Cloaked") || forceRemove) {
            warlordsPlayer.getCooldownManager().removeCooldownByName("Cloaked");
            warlordsPlayer.getEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
            warlordsPlayer.updateArmor();
        }
    }

    public WarlordsEntity getMarkedPlayer() {
        return markedPlayer;
    }

    public void addAndCheckDamageThreshold(float damageValue, WarlordsEntity warlordsPlayer) {
        addToDamageThreshold(damageValue);
        if (getDamageThreshold() >= 600) {
            OrderOfEviscerate.removeCloak(warlordsPlayer, false);
        }
    }

    public void addToDamageThreshold(float damageThreshold) {
        this.damageThreshold += damageThreshold;
    }

    public float getDamageThreshold() {
        return damageThreshold;
    }

    public static RegularCooldown<OrderOfEviscerate> giveCloak(@Nonnull WarlordsEntity wp, int duration) {
        wp.getCooldownManager().removeCooldownByName("Cloaked");
        RegularCooldown<OrderOfEviscerate> orderOfEviscerateCooldown = new RegularCooldown<>("Cloaked",
                "INVIS",
                OrderOfEviscerate.class,
                null,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                    wp.getEntity().removePotionEffect(PotionEffectType.INVISIBILITY);

                    LivingEntity wpEntity = wp.getEntity();
                    if (wpEntity instanceof Player) {
                        PlayerFilter.playingGame(wp.getGame())
                                    .enemiesOf(wp)
                                    .stream().map(WarlordsEntity::getEntity)
                                    .filter(Player.class::isInstance)
                                    .map(Player.class::cast)
                                    .forEach(enemyPlayer -> enemyPlayer.showPlayer(Warlords.getInstance(), (Player) wpEntity));
                    }
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 5 == 0) {
                        wp.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticksLeft, 0, true, false));

                        LivingEntity wpEntity = wp.getEntity();
                        if (wpEntity instanceof Player) {
                            ((Player) wpEntity).getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
                            PlayerFilter.playingGame(wp.getGame())
                                        .enemiesOf(wp)
                                        .stream().map(WarlordsEntity::getEntity)
                                        .filter(Player.class::isInstance)
                                        .map(Player.class::cast)
                                        .forEach(enemyPlayer -> enemyPlayer.hidePlayer(Warlords.getInstance(), (Player) wpEntity));
                        }
                    }
                })
        );
        wp.getCooldownManager().addCooldown(orderOfEviscerateCooldown);
        return orderOfEviscerateCooldown;
    }

    public void setMarkedPlayer(WarlordsEntity markedPlayer) {
        this.markedPlayer = markedPlayer;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isMasterUpgrade() {
        return masterUpgrade;
    }

    public void setMasterUpgrade(boolean masterUpgrade) {
        this.masterUpgrade = masterUpgrade;
    }
}

package com.ebicep.warlords.abilities;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.OrderOfEviscerateBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OrderOfEviscerate extends AbstractAbility implements OrangeAbilityIcon, Duration {

    public int numberOfFullResets = 0;
    public int numberOfHalfResets = 0;
    public int numberOfBackstabs = 0;

    protected float damageDoneWithOrder = 0;
    protected int mobsKilledWithOrder = 0;

    private int tickDuration = 160;
    private float damageThreshold = 0;
    private WarlordsEntity markedPlayer;

    public OrderOfEviscerate() {
        super("Order of Eviscerate", 0, 0, 50, 60);
    }

    @Override
    public void updateDescription(Player player) {
        TextComponent.Builder builder = Component.text("Cloak yourself for ").
                                                 append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD)).
                                                 append(Component.text(" seconds, granting you ")).
                                                 append(Component.text("40% ", NamedTextColor.YELLOW)).
                                                 append(Component.text("movement speed and making you ")).
                                                 append(Component.text("invisible ", NamedTextColor.YELLOW)).
                                                 append(Component.text("to the enemy for the duration. However, taking up to ")).
                                                 append(Component.text("600 ", NamedTextColor.RED)).
                                                 append(Component.text("fall damage or any type of ability damage will end your invisibility.\n\n")).
                                                 append(Component.text("All your attacks against an enemy will mark them vulnerable. Vulnerable enemies take ")).
                                                 append(Component.text("20% ", NamedTextColor.RED)).
                                                 append(Component.text("more damage. Additionally, enemies hit from behind take an additional ")).
                                                 append(Component.text("10% ", NamedTextColor.RED)).
                                                 append(Component.text("more damage.\n\n")).
                                                 append(Component.text("Successfully killing your mark will "))
                                                 .toBuilder();
        if (inPve) {
            int killReduction = pveMasterUpgrade ? 12 : 8; // 2 for shadow
            int assistReduction = pveMasterUpgrade ? 6 : 4; // 0 for shadow
            description = builder.append(Component.text("reduce ", NamedTextColor.YELLOW))
                                 .append(Component.text("your Shadow Step cooldown by "))
                                 .append(Component.text("2 ", NamedTextColor.YELLOW))
                                 .append(Component.text("seconds and Order of Eviscerate by "))
                                 .append(Component.text(killReduction, NamedTextColor.YELLOW))
                                 .append(Component.text("seconds. Assisting in killing your mark will "))
                                 .append(Component.text("reduce ", NamedTextColor.YELLOW))
                                 .append(Component.text("your Order of Eviscerate cooldown by "))
                                 .append(Component.text(assistReduction, NamedTextColor.YELLOW))
                                 .append(Component.text(" seconds."))
                                 .build();
        } else {
            description = builder.append(Component.text("reset ", NamedTextColor.YELLOW))
                                 .append(Component.text("both your Shadow Step and Order of Eviscerate's cooldown and refund the energy cost. " +
                                         "Assisting in killing your mark will only refund half the cooldown."))
                                 .build();
        }
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
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.5f, 0.7f);
        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Order of Eviscerate", 40, tickDuration, "BASE");

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
                    if (inPve) {
                        if (tempOrderOfEviscerate.damageDoneWithOrder >= 15000 && tempOrderOfEviscerate.mobsKilledWithOrder >= 6) {
                            ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.SERIAL_KILLER);
                        }
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 2 == 0) {
                        Utils.playGlobalSound(wp.getLocation(), Sound.AMBIENT_CAVE, 0.25f, 2);
                    }
                    EffectUtils.displayParticle(Particle.SMOKE_NORMAL, wp.getLocation(), 4, 0.2, 0.2, 0.2, 0.05);
                })
        ) {

            @Override
            public void doBeforeReductionFromAttacker(WarlordsDamageHealingEvent event) {
                //mark message here so it displays before damage
                WarlordsEntity victim = event.getWarlordsEntity();
                if (victim != wp) {
                    if (!Objects.equals(tempOrderOfEviscerate.getMarkedPlayer(), victim)) {
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" You have marked ", NamedTextColor.GRAY))
                                .append(Component.text(victim.getName(), NamedTextColor.YELLOW))
                                .append(Component.text("!", NamedTextColor.GRAY))
                        );
                    }
                    tempOrderOfEviscerate.setMarkedPlayer(victim);
                }
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (
                        Objects.equals(tempOrderOfEviscerate.getMarkedPlayer(), event.getWarlordsEntity()) &&
                                !LocationUtils.isLineOfSightAssassin(event.getWarlordsEntity(), event.getAttacker())
                ) {
                    numberOfBackstabs++;
                    return currentDamageValue * (inPve ? 2 : 1.3f);
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
                if (!Objects.equals(event.getWarlordsEntity(), tempOrderOfEviscerate.getMarkedPlayer())) {
                    return;
                }
                if (!inPve) {
                    this.setTicksLeft(0);
                } else {
                    removeCloak(wp, false);
                }
                if (isKiller) {
                    numberOfFullResets++;

                    if (inPve) {
                        tempOrderOfEviscerate.mobsKilledWithOrder++;
                    }

                    new GameRunnable(wp.getGame()) {
                        @Override
                        public void run() {
                            if (inPve) {
                                int reduction = pveMasterUpgrade ? 12 : 8;
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                        .append(Component.text(" You killed your mark,", NamedTextColor.GRAY))
                                        .append(Component.text(" your ultimate cooldown has been reduced by " + reduction + " seconds", NamedTextColor.YELLOW))
                                        .append(Component.text("!", NamedTextColor.GRAY))
                                );
                                for (ShadowStep shadowStep : wp.getAbilitiesMatching(ShadowStep.class)) {
                                    shadowStep.subtractCurrentCooldown(2);
                                    wp.updateItem(shadowStep);
                                }
                                for (OrderOfEviscerate orderOfEviscerate : wp.getAbilitiesMatching(OrderOfEviscerate.class)) {
                                    orderOfEviscerate.subtractCurrentCooldown(reduction);
                                    wp.updateItem(orderOfEviscerate);
                                }
                                if (pveMasterUpgrade2) {
                                    wp.getCooldownManager().limitCooldowns(RegularCooldown.class, "Cloaked Engagement", 2);
                                    wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            "Cloaked Engagement",
                                            "ENGAGE",
                                            OrderOfEviscerate.class,
                                            new OrderOfEviscerate(),
                                            wp,
                                            CooldownTypes.BUFF,
                                            cooldownManager -> {
                                            },
                                            8 * 20
                                    ) {
                                        @Override
                                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                            return currentDamageValue * 1.45f;
                                        }
                                    });
                                }
                            } else {
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                        .append(Component.text(" You killed your mark,", NamedTextColor.GRAY))
                                        .append(Component.text(" your cooldowns have been reset", NamedTextColor.YELLOW))
                                        .append(Component.text("!", NamedTextColor.GRAY))
                                );
                                for (ShadowStep shadowStep : wp.getAbilitiesMatching(ShadowStep.class)) {
                                    shadowStep.setCurrentCooldown(0);
                                    wp.updateItem(shadowStep);
                                }
                                for (OrderOfEviscerate orderOfEviscerate : wp.getAbilitiesMatching(OrderOfEviscerate.class)) {
                                    orderOfEviscerate.setCurrentCooldown(0);
                                    wp.updateItem(orderOfEviscerate);
                                }
                                wp.addEnergy(wp, name, energyCost.getBaseValue());
                            }
                        }
                    }.runTaskLater(2);
                } else {
                    numberOfHalfResets++;

                    new GameRunnable(wp.getGame()) {
                        @Override
                        public void run() {
                            if (inPve) {
                                int reduction = pveMasterUpgrade ? 6 : 4;
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                        .append(Component.text(" You assisted in killing your mark,", NamedTextColor.GRAY))
                                        .append(Component.text(" your ultimate cooldown has been reduced by " + reduction + " seconds", NamedTextColor.YELLOW))
                                        .append(Component.text("!", NamedTextColor.GRAY))
                                );
                                for (OrderOfEviscerate orderOfEviscerate : wp.getAbilitiesMatching(OrderOfEviscerate.class)) {
                                    orderOfEviscerate.subtractCurrentCooldown(reduction);
                                    wp.updateItem(orderOfEviscerate);
                                }
                            } else {
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                        .append(Component.text(" You assisted in killing your mark,", NamedTextColor.GRAY))
                                        .append(Component.text(" your cooldowns have been reduced by half", NamedTextColor.YELLOW))
                                        .append(Component.text("!", NamedTextColor.GRAY))
                                );
                                for (ShadowStep shadowStep : wp.getAbilitiesMatching(ShadowStep.class)) {
                                    shadowStep.setCurrentCooldown(shadowStep.getCooldownValue() / 2);
                                    wp.updateItem(shadowStep);
                                }
                                for (OrderOfEviscerate orderOfEviscerate : wp.getAbilitiesMatching(OrderOfEviscerate.class)) {
                                    orderOfEviscerate.setCurrentCooldown(orderOfEviscerate.getCooldownValue() / 2);
                                    wp.updateItem(orderOfEviscerate);
                                }
                                wp.addEnergy(wp, name, energyCost.getBaseValue() / 2f);
                            }
                        }
                    }.runTaskLater(2);
                }

                wp.playSound(wp.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 2);
            }
        });

        if (!FlagHolder.isPlayerHolderFlag(wp)) {
            giveCloak(wp, tickDuration);
        }

        return true;
    }

    public static void removeCloak(WarlordsEntity warlordsPlayer, boolean forceRemove) {
        if (warlordsPlayer.getCooldownManager().hasCooldownFromName("Cloaked") || forceRemove) {
            warlordsPlayer.getCooldownManager().removeCooldownByName("Cloaked");
            warlordsPlayer.removePotionEffect(PotionEffectType.INVISIBILITY);
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

    public static RegularCooldown<OrderOfEviscerate> giveCloak(@Nonnull WarlordsEntity wp, int tickDuration) {
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
                    wp.removePotionEffect(PotionEffectType.INVISIBILITY);

                    Entity wpEntity = wp.getEntity();
                    if (wpEntity instanceof Player) {
                        PlayerFilter.playingGame(wp.getGame())
                                    .enemiesOf(wp)
                                    .stream().map(WarlordsEntity::getEntity)
                                    .filter(Player.class::isInstance)
                                    .map(Player.class::cast)
                                    .forEach(enemyPlayer -> enemyPlayer.showPlayer(Warlords.getInstance(), (Player) wpEntity));
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 5 == 0) {
                        wp.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticksLeft, 0, true, false));

                        Entity wpEntity = wp.getEntity();
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

    public void addToDamageThreshold(float damageThreshold) {
        this.damageThreshold += damageThreshold;
    }

    public float getDamageThreshold() {
        return damageThreshold;
    }

    public void setMarkedPlayer(WarlordsEntity markedPlayer) {
        this.markedPlayer = markedPlayer;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new OrderOfEviscerateBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

}

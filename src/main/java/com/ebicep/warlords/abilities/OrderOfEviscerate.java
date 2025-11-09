package com.ebicep.warlords.abilities;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
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
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OrderOfEviscerate extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<OrderOfEviscerate, OrderOfEviscerate.OrderOfEviscerateStats>, OrderOfEviscerateLike {

    private final OrderOfEviscerateStats stats = new OrderOfEviscerateStats();
    private int tickDuration = 160;
    private float maxDamageThreshold = 600;
    private float vulnerableDamageBonus = 20;
    private int speedBuff = 40;
    private float orderKillCooldownReduction;
    private float orderAssistCooldownReduction;

    private RegularCooldown<OrderOfEviscerateData> cooldown = null;
    private int stacks = 0;

    public OrderOfEviscerate() {
        super(AbstractAbilityBuilder.create("orderOfEviscerate").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.maxDamageThreshold = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxDamageThreshold"), float.class);
        this.vulnerableDamageBonus = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("vulnerableDamageBonus"), float.class);
        this.speedBuff = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBuff"), int.class);
        this.orderKillCooldownReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("orderKillCooldownReduction"), float.class);
        this.orderAssistCooldownReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("orderAssistCooldownReduction"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        PlayerFilter.playingGame(wp.getGame())
                    .enemiesOf(wp)
                    .forEach(warlordsEntity -> {
                        warlordsEntity.playSound(wp.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.5f, 0.7f);
                    });
        wp.addSpeedModifier(wp, name, speedBuff, tickDuration);
        wp.getCooldownManager().removeCooldown(OrderOfEviscerateData.class, false);
        if (!FlagHolder.isPlayerHolderFlag(wp)) {
            giveCloak(wp, tickDuration);
        }
        OrderOfEviscerateData data = new OrderOfEviscerateData(maxDamageThreshold);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Order of Eviscerate",
                "ORDER",
                OrderOfEviscerateData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    wp.getSpeed().removeModifier(name);
                    removeCloak(wp, true);
                    if (inPve) {
                        if (data.damageDoneWithOrder >= 15000 && data.mobsKilledWithOrder >= 6) {
                            ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.SERIAL_KILLER);
                        }
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 2 == 0) {
                        PlayerFilter.playingGame(wp.getGame())
                                    .enemiesOf(wp)
                                    .forEach(warlordsEntity -> {
                                        warlordsEntity.playSound(wp.getLocation(), Sound.AMBIENT_CAVE, 0.25f, 2);
                                    });
                    }
                    EffectUtils.displayParticle(Particle.SMOKE, wp.getLocation(), 4, 0.2, 0.2, 0.2, 0.05);
                })
        ) {

            @Override
            public void doBeforeReductionFromAttacker(WarlordsDamageHealingEvent event) {
                //mark message here so it displays before damage
                WarlordsEntity victim = event.getWarlordsEntity();
                if (victim != wp) {
                    if (!Objects.equals(data.getMarkedPlayer(), victim)) {
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" You have ", NamedTextColor.GRAY))
                                                                      .append(Component.text("marked ", NamedTextColor.YELLOW))
                                                                      .append(Component.text(victim.getName() + "!", NamedTextColor.GRAY)));
                    }
                    data.setMarkedPlayer(victim);
                }
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (!Objects.equals(data.getMarkedPlayer(), event.getWarlordsEntity())) {
                    return currentDamageValue;
                }
                float damageBonus = vulnerableDamageBonus;
                if (pveMasterUpgrade && !LocationUtils.isLineOfSightAssassin(event.getWarlordsEntity(), event.getSource())) {
                    stats.numberOfBackstabs++;
                    damageBonus += 70;
                }
                return currentDamageValue * (1 + damageBonus / 100f);
            }

            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                data.addAndCheckDamageThreshold(currentDamageValue, wp);
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                data.damageDoneWithOrder += currentDamageValue;
            }

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (!Objects.equals(event.getWarlordsEntity(), data.getMarkedPlayer())) {
                    return;
                }
                if (!inPve) {
                    this.setTicksLeft(0);
                } else {
                    removeCloak(wp, false);
                }
                if (isKiller) {
                    stats.numberOfFullResets++;
                    if (inPve) {
                        data.mobsKilledWithOrder++;
                    }
                    new GameRunnable(wp.getGame()) {

                        @Override
                        public void run() {
                            if (inPve) {
                                if (stacks < 2) {
                                    stacks++;
                                }
                                int reduction = pveMasterUpgrade ? 12 : 8;
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                        .append(Component.text(" You killed your mark,", NamedTextColor.GRAY))
                                        .append(Component.text(" your ultimate cooldown has been reduced by " + reduction + " seconds",
                                                NamedTextColor.YELLOW
                                        ))
                                        .append(Component.text("!", NamedTextColor.GRAY)));
                                for (ShadowStep shadowStep : wp.getAbilitiesMatching(ShadowStep.class)) {
                                    shadowStep.subtractCurrentCooldown(2);
                                }
                                for (OrderOfEviscerate orderOfEviscerate : wp.getAbilitiesMatching(OrderOfEviscerate.class)) {
                                    orderOfEviscerate.subtractCurrentCooldown(reduction);
                                }
                                if (pveMasterUpgrade2 && cooldown == null) {
                                    wp.getCooldownManager().addCooldown(cooldown = new RegularCooldown<>(
                                            "Cloaked Engagement 1",
                                            "ENGAGE 1",
                                            OrderOfEviscerateData.class,
                                            null,
                                            wp,
                                            CooldownTypes.BUFF,
                                            cooldownManager -> {
                                            },
                                            cooldownManager -> {
                                                cooldown = null;
                                                stacks = 0;
                                                },
                                            8 * 20
                                    ) {

                                          @Override
                                          public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                              return currentDamageValue * (1 + 0.4f * stacks);
                                          }
                                      });
                                } else {
                                    cooldown.setTicksLeft(8 * 20);
                                    cooldown.setName("Cloaked Engagement " + stacks);
                                    cooldown.setNameAbbreviation("ENGAGE " + stacks);
                                }
                            } else {
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                        .append(Component.text(" You killed your mark, ", NamedTextColor.GRAY))
                                        .append(Component.text(OrderOfEviscerate.this.name, NamedTextColor.YELLOW))
                                        .append(Component.text("'s cooldown was reduced by ", NamedTextColor.GRAY))
                                        .append(Component.text(NumberFormat.formatOptionalHundredths(orderKillCooldownReduction), NamedTextColor.GOLD))
                                        .append(Component.text(" seconds and ", NamedTextColor.GRAY))
                                        .append(Component.text("Soul Switch", NamedTextColor.YELLOW))
                                        .append(Component.text("'s cooldown was reset.", NamedTextColor.GRAY))
                                );
                                for (OrderOfEviscerate orderOfEviscerate : wp.getAbilitiesMatching(OrderOfEviscerate.class)) {
                                    orderOfEviscerate.subtractCurrentCooldown(orderKillCooldownReduction);
                                }
                                for (SoulSwitch soulSwitch : wp.getAbilitiesMatching(SoulSwitch.class)) {
                                    soulSwitch.setCurrentCooldown(0);
                                }
                                wp.addEnergy(wp, name, energyCost.getBaseValue());
                            }
                            wp.getSpec().resetAbilityCD(wp);
                        }
                    }.runTaskLater(2);
                } else {
                    stats.numberOfHalfResets++;
                    new GameRunnable(wp.getGame()) {

                        @Override
                        public void run() {
                            if (inPve) {
                                int reduction = pveMasterUpgrade ? 6 : 4;
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                        .append(Component.text(" You assisted in killing your mark,", NamedTextColor.GRAY))
                                        .append(Component.text(" your ultimate cooldown has been reduced by " + reduction + " seconds",
                                                NamedTextColor.YELLOW
                                        ))
                                        .append(Component.text("!", NamedTextColor.GRAY)));
                                for (OrderOfEviscerate orderOfEviscerate : wp.getAbilitiesMatching(OrderOfEviscerate.class)) {
                                    orderOfEviscerate.subtractCurrentCooldown(reduction);
                                }
                            } else {
                                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                        .append(Component.text(" You assisted in killing your mark, ", NamedTextColor.GRAY))
                                        .append(Component.text(OrderOfEviscerate.this.name, NamedTextColor.YELLOW))
                                        .append(Component.text("'s cooldown was reduced by ", NamedTextColor.GRAY))
                                        .append(Component.text(NumberFormat.formatOptionalHundredths(orderAssistCooldownReduction), NamedTextColor.GOLD))
                                        .append(Component.text(" seconds and ", NamedTextColor.GRAY))
                                        .append(Component.text("Soul Switch", NamedTextColor.YELLOW))
                                        .append(Component.text("'s cooldown was reset.", NamedTextColor.GRAY))
                                );
                                for (OrderOfEviscerate orderOfEviscerate : wp.getAbilitiesMatching(OrderOfEviscerate.class)) {
                                    orderOfEviscerate.subtractCurrentCooldown(orderAssistCooldownReduction);
                                }
                                for (SoulSwitch soulSwitch : wp.getAbilitiesMatching(SoulSwitch.class)) {
                                    soulSwitch.setCurrentCooldown(0);
                                }
                                wp.addEnergy(wp, name, energyCost.getBaseValue() / 2f);
                            }
                            wp.getSpec().resetAbilityCD(wp);
                        }
                    }.runTaskLater(2);
                }
                wp.playSound(wp.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 2);
            }
        });

        return true;
    }

    @Override
    public void updateDescription(Player player) {
        AbilityDescriptionBuilder builder = AbilityDescriptionBuilder
                .create("Cloak yourself for ")
                .durationTicks(tickDuration)
                .text(", granting you ")
                .percent(speedBuff, NamedTextColor.WHITE)
                .text(" extra movement speed and making you ")
                .text("INVIS", NamedTextColor.DARK_GREEN)
                .text(" to the enemy for the duration. However, taking up to ")
                .text(maxDamageThreshold, NamedTextColor.RED)
                .text(" fall damage or any type of ability damage will end your invisibility.")
                .emptyLine()
                .text("All your attacks against an enemy will mark them vulnerable. Vulnerable enemies take ")
                .percent(vulnerableDamageBonus, NamedTextColor.RED)
                .text(" more damage.")
                .emptyLine()
                .text("Successfully killing your mark will ");
        if (inPve) {
            // 2 for shadow
            int killReduction = pveMasterUpgrade ? 12 : 8;
            // 0 for shadow
            int assistReduction = pveMasterUpgrade ? 6 : 4;
            builder.text("reduce", NamedTextColor.YELLOW)
                   .text(" your Shadow Step cooldown by ")
                   .text(2, NamedTextColor.GOLD)
                   .text(" seconds and Order of Eviscerate by ")
                   .text(killReduction, NamedTextColor.GOLD)
                   .text(" seconds. Assisting in killing your mark will ")
                   .text("reduce", NamedTextColor.YELLOW)
                   .text(" your Order of Eviscerate cooldown by ")
                   .text(assistReduction, NamedTextColor.GOLD)
                   .text(" seconds.");
        } else {
            builder.text(" reduce your Order of Eviscerate's cooldown by ")
                   .durationSeconds(orderKillCooldownReduction)
                   .text(". Assists only reduce ")
                   .durationSeconds(orderAssistCooldownReduction)
                   .emptyLine()
                   .text("If your mark dies, the cooldown of Soul Switch is reset.");
        }
        description = builder.build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new OrderOfEviscerateBranch(abilityTree, this);
    }

    public static void giveCloak(@Nonnull WarlordsEntity wp, int tickDuration) {
        wp.getCooldownManager().removeCooldownByName("Cloaked");
        RegularCooldown<OrderOfEviscerateData> orderOfEviscerateCooldown = new RegularCooldown<>("Cloaked",
                "INVIS",
                OrderOfEviscerateData.class,
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
                                    .stream()
                                    .map(WarlordsEntity::getEntity)
                                    .filter(Player.class::isInstance)
                                    .map(Player.class::cast)
                                    .forEach(enemyPlayer -> enemyPlayer.showPlayer(Warlords.getInstance(), (Player) wpEntity));
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        wp.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticksLeft, 0, true, false));
                        Entity wpEntity = wp.getEntity();
                        if (wpEntity instanceof Player) {
                            PlayerFilter.playingGame(wp.getGame())
                                        .enemiesOf(wp)
                                        .stream()
                                        .map(WarlordsEntity::getEntity)
                                        .filter(Player.class::isInstance)
                                        .map(Player.class::cast)
                                        .forEach(enemyPlayer -> enemyPlayer.hidePlayer(Warlords.getInstance(), (Player) wpEntity));
                        }
                    }
                })
        );
        wp.getCooldownManager().addCooldown(orderOfEviscerateCooldown);
    }

    public static void removeCloak(WarlordsEntity warlordsPlayer, boolean forceRemove) {
        if (warlordsPlayer.getCooldownManager().hasCooldownFromName("Cloaked") || forceRemove) {
            warlordsPlayer.getCooldownManager().removeCooldownByName("Cloaked");
            warlordsPlayer.removePotionEffect(PotionEffectType.INVISIBILITY);
            warlordsPlayer.updateArmor();
        }
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public OrderOfEviscerateStats getAbilityStats() {
        return stats;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }

    public float getVulnerableDamageBonus() {
        return vulnerableDamageBonus;
    }

    public void setVulnerableDamageBonus(float vulnerableDamageBonus) {
        this.vulnerableDamageBonus = vulnerableDamageBonus;
    }

    public static class OrderOfEviscerateData {

        private final float maxDamageThreshold;

        private float damageDoneWithOrder = 0;

        private int mobsKilledWithOrder = 0;

        private float damageThreshold = 0;

        private WarlordsEntity markedPlayer;

        public OrderOfEviscerateData(float maxDamageThreshold) {
            this.maxDamageThreshold = maxDamageThreshold;
        }

        public void addAndCheckDamageThreshold(float damageValue, WarlordsEntity warlordsPlayer) {
            addToDamageThreshold(damageValue);
            if (getDamageThreshold() >= maxDamageThreshold) {
                OrderOfEviscerate.removeCloak(warlordsPlayer, false);
            }
        }

        public void addToDamageThreshold(float damageThreshold) {
            this.damageThreshold += damageThreshold;
        }

        public float getDamageThreshold() {
            return damageThreshold;
        }

        public WarlordsEntity getMarkedPlayer() {
            return markedPlayer;
        }

        public void setMarkedPlayer(WarlordsEntity markedPlayer) {
            this.markedPlayer = markedPlayer;
        }

    }

    public static class OrderOfEviscerateStats extends AbstractAbilityStats<OrderOfEviscerate, OrderOfEviscerateStats> {

        @Field("number_of_full_resets")
        private int numberOfFullResets = 0;

        @Field("number_of_half_resets")
        private int numberOfHalfResets = 0;

        @Field("number_of_backstabs")
        private int numberOfBackstabs = 0;

        @Override
        public Class<OrderOfEviscerateStats> getClazz() {
            return OrderOfEviscerateStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Number of Full Resets", numberOfFullResets));
            statsDisplay.add(new AbilityStatDisplay("Number of Half Resets", numberOfHalfResets));
            statsDisplay.add(new AbilityStatDisplay("Number of Backstabs", numberOfBackstabs));
            return statsDisplay;
        }

        @Override
        public OrderOfEviscerateStats merge(OrderOfEviscerateStats other, int multiplier) {
            OrderOfEviscerateStats stats = super.merge(other, multiplier);
            stats.numberOfFullResets = this.numberOfFullResets + other.numberOfFullResets * multiplier;
            stats.numberOfHalfResets = this.numberOfHalfResets + other.numberOfHalfResets * multiplier;
            stats.numberOfBackstabs = this.numberOfBackstabs + other.numberOfBackstabs * multiplier;
            return stats;
        }

        @Override
        public OrderOfEviscerateStats create() {
            return new OrderOfEviscerateStats();
        }

    }

}

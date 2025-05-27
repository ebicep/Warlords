package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.LastStandBranch;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LastStand extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<LastStand, LastStand.LastStandStats> {

    private final LastStandStats stats = new LastStandStats();
    private int radius = 7;
    private int selfTickDuration = 240;
    private int allyTickDuration = 120;
    private int selfDamageReductionPercent = 35;
    private int teammateDamageReductionPercent = 35;

    public LastStand() {
        super(AbstractAbilityBuilder.create("lastStand").pvp());
    }

    public LastStand(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), int.class);
        this.selfTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("selfTickDuration"), int.class);
        this.allyTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("allyTickDuration"), int.class);
        this.selfDamageReductionPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("selfDamageReductionPercent"), int.class);
        this.teammateDamageReductionPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                builder.getAppendedFieldName("teammateDamageReductionPercent"),
                int.class
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Enter a defensive stance, reducing all damage you take by ")
                                               .percent(selfDamageReductionPercent, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" for ")
                                               .durationTicks(selfTickDuration)
                                               .text(" and also reduces all damage allies within")
                                               .blocks(radius)
                                               .text(" by ")
                                               .percent(teammateDamageReductionPercent, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" for ")
                                               .durationTicks(allyTickDuration)
                                               .text(". You are healed for the amount of damage prevented on allies." + (inPve ? "Additionally, constantly take aggro of nearby mobs." : ""))
                                               .build();
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "warrior.laststand.activation", 2, 1);
        LastStandData data = new LastStandData();
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        if (pveMasterUpgrade2) {
            for (SeismicWaveDefender ability : wp.getAbilitiesMatching(SeismicWaveDefender.class)) {
                modifiers.add(ability.getCooldown().addMultiplicativeModifierAdd("Enduring Defense", -.5f));
                modifiers.add(ability.getEnergyCost().addOverridingModifier("Enduring Defense", 30f));
            }
            for (GroundSlamDefender ability : wp.getAbilitiesMatching(GroundSlamDefender.class)) {
                modifiers.add(ability.getCooldown().addMultiplicativeModifierAdd("Enduring Defense", -.5f));
            }
        }
        RegularCooldown<LastStandData> lastStandCooldown = new RegularCooldown<>(name, "LAST", LastStandData.class, data, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, cooldownManager -> {
            ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.HARDENED_SCALES);
            if (pveMasterUpgrade2) {
                modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
            }
        }, selfTickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
            if ((pveMasterUpgrade || pveMasterUpgrade2) && ticksLeft % 15 == 0) {
                for (WarlordsEntity we : PlayerFilter.entitiesAround(wp, radius, radius, radius).aliveEnemiesOf(wp).closestFirst(wp)) {
                    if (we instanceof WarlordsNPC) {
                        ((WarlordsNPC) we).getMob().setTarget(wp);
                    }
                }
            }
        })
        ) {

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float afterValue = currentDamageValue * convertToDivisionDecimal(selfDamageReductionPercent);
                data.addAmountPrevented(currentDamageValue - afterValue);
                return afterValue;
            }

        };
        if (pveMasterUpgrade) {
            wp.addKnockbackModifier(wp, name, -50, lastStandCooldown);
        }
        wp.getCooldownManager().addCooldown(lastStandCooldown);
        for (WarlordsEntity standTarget : PlayerFilter.entitiesAround(wp, radius, radius, radius).aliveTeammatesOf(wp).excluding(wp)) {
            stats.targetsLastStanded++;
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), standTarget.getLocation(), Particle.HAPPY_VILLAGER);
            standTarget.getCooldownManager().addCooldown(new RegularCooldown<>(name, "LAST", LastStandData.class, data, wp, CooldownTypes.ABILITY, cooldownManager -> {
            }, allyTickDuration
            ) {

                float amountPrevented = 0;

                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    float newCurrentDamageValue = currentDamageValue * convertToDivisionDecimal(teammateDamageReductionPercent);
                    amountPrevented = currentDamageValue - newCurrentDamageValue;
                    data.addAmountPrevented(amountPrevented);
                    return newCurrentDamageValue;
                }

                @Override
                public void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    data.addAmountPrevented(amountPrevented);
                    wp.addInstance(InstanceBuilder.healing()
                                                  .ability(LastStand.this)
                                                  .source(wp)
                                                  .value(amountPrevented)
                                                  .showAsCrit(isCrit)
                                                  .flags(InstanceFlags.LAST_STAND_FROM_SHIELD));
                }

                @Override
                public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    wp.addInstance(InstanceBuilder.healing()
                                                  .ability(LastStand.this)
                                                  .source(wp)
                                                  .value(amountPrevented)
                                                  .showAsCrit(isCrit)
                    );
                }
            });
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your Last Stand is now protecting ", NamedTextColor.GRAY))
                                                          .append(Component.text(standTarget.getName(), NamedTextColor.YELLOW))
                                                          .append(Component.text("!", NamedTextColor.GRAY)));
            standTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN.append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                                                      .append(Component.text("Last Stand", NamedTextColor.YELLOW))
                                                                      .append(Component.text(" is now protecting you for ", NamedTextColor.GRAY))
                                                                      .append(Component.text(format(allyTickDuration / 20f), NamedTextColor.GOLD))
                                                                      .append(Component.text(" seconds!", NamedTextColor.GRAY)));
        }
        Location loc = wp.getEyeLocation();
        loc.setPitch(0);
        loc.setYaw(0);
        Matrix4d matrix = new Matrix4d();
        int distance = radius / 2;
        for (int i = 0; i < distance; i++) {
            loc.setYaw(loc.getYaw() + 360F / distance);
            matrix.updateFromLocation(loc);
            for (int c = 0; c < 20; c++) {
                double angle = c / 20D * Math.PI * 2;
                double width = 1.2;
                loc.getWorld()
                   .spawnParticle(Particle.FLAME, matrix.translateVector(wp.getWorld(), distance, Math.sin(angle) * width, Math.cos(angle) * width), 1, 0, 0, 0, 0, null, true);
            }
            for (int c = 0; c < 10; c++) {
                double width = 0.6;
                double angle = c / 10D * Math.PI * 2;
                loc.getWorld()
                   .spawnParticle(Particle.DUST,
                           matrix.translateVector(wp.getWorld(), distance, Math.sin(angle) * width, Math.cos(angle) * width),
                           1,
                           0,
                           0,
                           0,
                           0,
                           new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1),
                           true
                   );
            }
        }
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LastStandBranch(abilityTree, this);
    }

    @Override
    public void multiplyTickDuration(float multiplier) {
        this.selfTickDuration *= multiplier;
        this.allyTickDuration *= multiplier;
    }

    @Override
    public int getTickDuration() {
        return selfTickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.selfTickDuration = tickDuration;
    }

    @Override
    public LastStandStats getAbilityStats() {
        return stats;
    }

    public int getSelfDamageReduction() {
        return selfDamageReductionPercent;
    }

    public void setSelfDamageReductionPercent(int selfDamageReductionPercent) {
        this.selfDamageReductionPercent = selfDamageReductionPercent;
    }

    public int getTeammateDamageReduction() {
        return teammateDamageReductionPercent;
    }

    public void setTeammateDamageReductionPercent(int teammateDamageReductionPercent) {
        this.teammateDamageReductionPercent = teammateDamageReductionPercent;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public static class LastStandData {

        private float amountPrevented = 0;

        public void addAmountPrevented(float amountPrevented) {
            this.amountPrevented += amountPrevented;
        }

        public float getAmountPrevented() {
            return amountPrevented;
        }

    }

    public static class LastStandStats extends AbstractAbilityStats<LastStand, LastStandStats> {

        @Field("targets_last_standed")
        private int targetsLastStanded = 0;

        @Override
        public Class<LastStandStats> getClazz() {
            return LastStandStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Last Standed", targetsLastStanded));
            return statsDisplay;
        }

        @Override
        public LastStandStats merge(LastStandStats other, int multiplier) {
            LastStandStats stats = super.merge(other, multiplier);
            stats.targetsLastStanded = this.targetsLastStanded + other.targetsLastStanded * multiplier;
            return stats;
        }

        @Override
        public LastStandStats create() {
            return new LastStandStats();
        }

    }

}

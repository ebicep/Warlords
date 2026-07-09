package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SacrificialStand extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<SacrificialStand, SacrificialStand.SacrificialStandStats> {

    private final SacrificialStandStats stats = new SacrificialStandStats();
    private int radius = 7;
    private int tickDuration = 120;
    private int damageReductionPercent = 40;
    private int allyHealMultiplierPercent = 200;

    public SacrificialStand() {
        super(AbstractAbilityBuilder.create("sacrificialStand").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.damageReductionPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReductionPercent"), int.class);
        this.allyHealMultiplierPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("allyHealMultiplierPercent"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        LastStand.playActivationSound(wp);
        Set<WarlordsEntity> snapshottedAllies = new HashSet<>();
        for (WarlordsEntity ally : PlayerFilter.entitiesAround(wp, radius, radius, radius).aliveTeammatesOf(wp).excluding(wp)) {
            snapshottedAllies.add(ally);
        }
        SacrificialStandData data = new SacrificialStandData(snapshottedAllies);
        final float[] amountPrevented = {0};
        RegularCooldown<SacrificialStandData> casterCooldown = new RegularCooldown<>(
                name,
                "LAST",
                SacrificialStandData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                tickDuration
        );
        casterCooldown.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name,
                    convertToDivisionDecimal(damageReductionPercent),
                    contribution -> {
                        amountPrevented[0] = Math.abs(contribution);
                        data.addAmountPrevented(Math.abs(contribution));
                        stats.addDamagePrevented(Math.abs(contribution));
                    }
            );
        });
        casterCooldown.addModifier(Modifier.ON_INCOMING_SHIELD_DAMAGE, (event, currentDamageValue, isCrit) -> {
            healSnapshottedAllies(wp, data, amountPrevented[0], isCrit, true);
        });
        casterCooldown.addModifier(Modifier.ON_INCOMING_DAMAGE, (event, currentDamageValue, isCrit) -> {
            healSnapshottedAllies(wp, data, amountPrevented[0], isCrit, false);
        });
        wp.getCooldownManager().addCooldown(casterCooldown);
        for (WarlordsEntity ally : snapshottedAllies) {
            stats.targetsProtected++;
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), ally.getLocation(), Particle.HAPPY_VILLAGER);
            ally.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "LAST",
                    SacrificialStandData.class,
                    data,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    tickDuration
            ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                currentDamageValue.addModifier(
                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name,
                        convertToDivisionDecimal(damageReductionPercent),
                        contribution -> stats.addDamagePrevented(Math.abs(contribution))
                );
            }));
            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your Sacrificial Stand is now protecting ", NamedTextColor.GRAY))
                                                          .append(Component.text(ally.getName(), NamedTextColor.YELLOW))
                                                          .append(Component.text("!", NamedTextColor.GRAY)));
            ally.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN.append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                                               .append(Component.text("Sacrificial Stand", NamedTextColor.YELLOW))
                                                               .append(Component.text(" is now protecting you for ", NamedTextColor.GRAY))
                                                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                                               .append(Component.text(" seconds!", NamedTextColor.GRAY)));
        }
        LastStand.playActivationParticles(wp, radius);
        return true;
    }

    private void healSnapshottedAllies(WarlordsEntity wp, SacrificialStandData data, float amountPrevented, boolean isCrit, boolean fromShield) {
        if (amountPrevented <= 0) {
            return;
        }
        float heal = amountPrevented * allyHealMultiplierPercent / 100f;
        for (WarlordsEntity ally : data.getSnapshottedAllies()) {
            if (ally.isDead()) {
                continue;
            }
            InstanceBuilder builder = InstanceBuilder.healing()
                                                     .ability(this)
                                                     .source(wp)
                                                     .value(heal)
                                                     .showAsCrit(isCrit);
            if (fromShield) {
                builder.flags(InstanceFlags.LAST_STAND_FROM_SHIELD);
            }
            ally.addInstance(builder);
            stats.addHealingDone(heal);
        }
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Enter a defensive stance, reducing all damage you and your allies within ")
                                               .blocks(radius)
                                               .text(" take by ")
                                               .percent(damageReductionPercent, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" for ")
                                               .durationTicks(tickDuration)
                                               .text(". Your allies are healed for ")
                                               .percent(allyHealMultiplierPercent, NamedTextColor.GREEN)
                                               .text(" of the amount of damage prevented on yourself.")
                                               .build();
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
    public SacrificialStandStats getAbilityStats() {
        return stats;
    }

    public int getRadius() {
        return radius;
    }

    public int getDamageReductionPercent() {
        return damageReductionPercent;
    }

    public int getAllyHealMultiplierPercent() {
        return allyHealMultiplierPercent;
    }

    public static class SacrificialStandData {

        private final Set<WarlordsEntity> snapshottedAllies;
        private float amountPrevented = 0;

        public SacrificialStandData(Set<WarlordsEntity> snapshottedAllies) {
            this.snapshottedAllies = snapshottedAllies;
        }

        public Set<WarlordsEntity> getSnapshottedAllies() {
            return snapshottedAllies;
        }

        public void addAmountPrevented(float amountPrevented) {
            this.amountPrevented += amountPrevented;
        }

        public float getAmountPrevented() {
            return amountPrevented;
        }

    }

    public static class SacrificialStandStats extends AbstractAbilityStats<SacrificialStand, SacrificialStandStats> {

        @Field("targets_protected")
        private int targetsProtected = 0;

        @Field("healing_done")
        private float healingDone = 0;

        @Field("damage_prevented")
        private float damagePrevented = 0;

        public void addHealingDone(float healingDone) {
            this.healingDone += healingDone;
        }

        public void addDamagePrevented(float damagePrevented) {
            this.damagePrevented += damagePrevented;
        }

        @Override
        public Class<SacrificialStandStats> getClazz() {
            return SacrificialStandStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Protected", targetsProtected));
            statsDisplay.add(new AbilityStatDisplay("Healing Done", healingDone));
            statsDisplay.add(new AbilityStatDisplay("Damage Prevented", damagePrevented));
            return statsDisplay;
        }

        @Override
        public SacrificialStandStats merge(SacrificialStandStats other, int multiplier) {
            SacrificialStandStats stats = super.merge(other, multiplier);
            stats.targetsProtected = this.targetsProtected + other.targetsProtected * multiplier;
            stats.healingDone = this.healingDone + other.healingDone * multiplier;
            stats.damagePrevented = this.damagePrevented + other.damagePrevented * multiplier;
            return stats;
        }

        @Override
        public SacrificialStandStats create() {
            return new SacrificialStandStats();
        }

    }

}

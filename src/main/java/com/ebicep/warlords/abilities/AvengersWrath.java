package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.AvengersWrathBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class AvengersWrath extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<AvengersWrath, AvengersWrath.AvengersWrathStats> {

    private final AvengersWrathStats stats = new AvengersWrathStats();
    private int tickDuration = 240;
    private float energyPerSecond = 20;
    private int maxTargets = 2;
    private int hitRadius = 5;

    public AvengersWrath() {
        super(AbstractAbilityBuilder.create("avengersWrath").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.energyPerSecond = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyPerSecond"), float.class);
        this.maxTargets = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxTargets"), int.class);
        this.hitRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hitRadius"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Burst with incredible holy power, causing your Avenger's Strikes to hit up to ")
                                               .text(maxTargets, NamedTextColor.BLUE)
                                               .text(" additional enemies that are within ")
                                               .blocks(hitRadius)
                                               .text(" of your target. Your energy per second is increased by ")
                                               .energy(energyPerSecond)
                                               .text(" for the duration of the effect. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "paladin.avengerswrath.activation", 2, 1);
        wp.getCooldownManager().removeCooldown(AvengersWrathData.class, false);
        AvengersWrathData data = new AvengersWrathData();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(name, "WRATH", AvengersWrathData.class, data, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
            if (ticksElapsed % 4 == 0) {
                EffectUtils.displayParticle(Particle.EFFECT, wp.getLocation().add(0, 1.2, 0), 6, 0.3F, 0.1F, 0.3F, 0.2F);
            }
        })
        ) {

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (!event.getCause().equals("Avenger's Strike") || event.getFlags().contains(InstanceFlags.AVENGER_WRATH_STRIKE)) {
                    return;
                }
                WarlordsEntity warlordsEntity = event.getWarlordsEntity();
                stats.targetsStruckDuringWrath++;
                data.targetsStruckDuringWrath++;
                EnumSet<InstanceFlags> flags = EnumSet.of(InstanceFlags.AVENGER_WRATH_STRIKE);
                if (event.getFlags().contains(InstanceFlags.STRIKE_IN_CONS)) {
                    flags.add(InstanceFlags.STRIKE_IN_CONS);
                }
                if (pveMasterUpgrade2) {
                    warlordsEntity.addInstance(InstanceBuilder.damage().cause("Avenger's Strike").source(wp).value(event).flags(flags));
                    stats.extraTargetsStruck++;
                    data.extraTargetsStruck++;
                }
                for (WarlordsEntity wrathTarget : PlayerFilter.entitiesAround(warlordsEntity, hitRadius, hitRadius, hitRadius)
                                                              .aliveEnemiesOf(wp)
                                                              .closestFirst(warlordsEntity)
                                                              .excluding(warlordsEntity)
                                                              .limit(maxTargets)) {
                    stats.extraTargetsStruck++;
                    stats.targetsStruckDuringWrath++;
                    data.extraTargetsStruck++;
                    data.targetsStruckDuringWrath++;
                    wrathTarget.addInstance(InstanceBuilder.damage().cause("Avenger's Strike").source(wp).value(event).flags(flags));
                    Bukkit.getPluginManager().callEvent(new WarlordsStrikeEvent(wp, AvengersWrath.this, wrathTarget));
                    wrathTarget.subtractEnergy(name, 10, true);
                }
            }

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (isKiller) {
                    stats.targetsKilledDuringWrath++;
                    data.targetsKilledDuringWrath++;
                }
            }

            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                return energyGainPerTick + energyPerSecond / 20f;
            }
        });
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new AvengersWrathBranch(abilityTree, this);
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
    public AvengersWrathStats getAbilityStats() {
        return stats;
    }

    public float getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(float energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public int getHitRadius() {
        return hitRadius;
    }

    public void setHitRadius(int hitRadius) {
        this.hitRadius = hitRadius;
    }

    public static class AvengersWrathData {

        private int extraTargetsStruck = 0;
        private int targetsStruckDuringWrath = 0;
        private int targetsKilledDuringWrath = 0;

        public int getExtraTargetsStruck() {
            return extraTargetsStruck;
        }

        public int getTargetsStruckDuringWrath() {
            return targetsStruckDuringWrath;
        }

        public int getTargetsKilledDuringWrath() {
            return targetsKilledDuringWrath;
        }

    }

    public static class AvengersWrathStats extends AbstractAbilityStats<AvengersWrath, AvengersWrathStats> {

        @Field("extra_targets_struck")
        private int extraTargetsStruck = 0;

        @Field("targets_struck_during_wrath")
        private int targetsStruckDuringWrath = 0;

        @Field("targets_killed_during_wrath")
        private int targetsKilledDuringWrath = 0;

        @Override
        public Class<AvengersWrathStats> getClazz() {
            return AvengersWrathStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Extra Targets Struck", extraTargetsStruck));
            statsDisplay.add(new AbilityStatDisplay("Targets Struck During Wrath", targetsStruckDuringWrath));
            statsDisplay.add(new AbilityStatDisplay("Targets Killed During Wrath", targetsKilledDuringWrath));
            return statsDisplay;
        }

        @Override
        public AvengersWrathStats merge(AvengersWrathStats other, int multiplier) {
            AvengersWrathStats stats = super.merge(other, multiplier);
            stats.extraTargetsStruck = this.extraTargetsStruck + other.extraTargetsStruck * multiplier;
            stats.targetsStruckDuringWrath = this.targetsStruckDuringWrath + other.targetsStruckDuringWrath * multiplier;
            stats.targetsKilledDuringWrath = this.targetsKilledDuringWrath + other.targetsKilledDuringWrath * multiplier;
            return stats;
        }

        @Override
        public AvengersWrathStats create() {
            return new AvengersWrathStats();
        }

        public int getTargetsStruckDuringWrath() {
            return targetsStruckDuringWrath;
        }

        public int getTargetsKilledDuringWrath() {
            return targetsKilledDuringWrath;
        }

    }

}

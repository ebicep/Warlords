package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.cooldowns.instances.CustomInstanceFlags;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class InstanceBuilder {

    public static InstanceBuilder create(InstanceType instanceType) {
        return new InstanceBuilder(instanceType);
    }

    private final InstanceType instanceType;
    private WarlordsEntity target;
    private WarlordsEntity source;
    @Nullable
    private AbstractAbility ability = null;
    private String cause;
    private float min;
    private float max;
    private float critChance = 0;
    private float critMultiplier = 100;
    private EnumSet<InstanceFlags> flags = EnumSet.noneOf(InstanceFlags.class);
    private List<CustomInstanceFlags> customFlags = Collections.emptyList();
    @Nullable
    private UUID uuid = null;

    public InstanceBuilder(InstanceType instanceType) {
        this.instanceType = instanceType;
    }

    public InstanceBuilder target(WarlordsEntity target) {
        this.target = target;
        return this;
    }

    public InstanceBuilder source(WarlordsEntity source) {
        this.source = source;
        return this;
    }

    public InstanceBuilder ability(AbstractAbility ability) {
        this.ability = ability;
        this.cause = ability.getName();
        return this;
    }

    public InstanceBuilder cause(String cause) {
        this.cause = cause;
        return this;
    }

    public InstanceBuilder value(Value.RangedValue rangedValue) {
        this.min = rangedValue.min().getCalculatedValue();
        this.max = rangedValue.max().getCalculatedValue();
        return this;
    }

    public InstanceBuilder value(float min, float max) {
        this.min = min;
        this.max = max;
        return this;
    }

    public InstanceBuilder value(float value) {
        this.min = value;
        this.max = value;
        return this;
    }

    public InstanceBuilder value(Value.RangedValueCritable rangedValueCritable) {
        this.min = rangedValueCritable.min().getCalculatedValue();
        this.max = rangedValueCritable.max().getCalculatedValue();
        this.critChance = rangedValueCritable.critChance().getCalculatedValue();
        this.critMultiplier = rangedValueCritable.critMultiplier().getCalculatedValue();
        return this;
    }

    public InstanceBuilder crit(float critChance, float critMultiplier) {
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        return this;
    }

    public InstanceBuilder flags(InstanceFlags... flags) {
        this.flags = EnumSet.of(flags[0], flags);
        return this;
    }

    public InstanceBuilder customFlags(CustomInstanceFlags... customFlags) {
        this.customFlags = List.of(customFlags);
        return this;
    }

    public InstanceBuilder uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public WarlordsDamageHealingEvent build() {
        return new WarlordsDamageHealingEvent(
                instanceType,
                target,
                source,
                ability,
                cause,
                min,
                max,
                critChance,
                critMultiplier,
                flags,
                customFlags,
                uuid
        );
    }

    public enum InstanceType {
        DAMAGE, HEALING
    }


}

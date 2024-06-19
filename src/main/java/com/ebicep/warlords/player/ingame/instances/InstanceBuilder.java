package com.ebicep.warlords.player.ingame.instances;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class InstanceBuilder {

    public static InstanceBuilder create(InstanceType instanceType) {
        return new InstanceBuilder(instanceType);
    }

    public static InstanceBuilder damage() {
        return new InstanceBuilder(InstanceType.DAMAGE);
    }

    public static InstanceBuilder melee() {
        return new InstanceBuilder(InstanceType.DAMAGE).cause("");
    }

    public static InstanceBuilder fall() {
        return new InstanceBuilder(InstanceType.DAMAGE).cause("Fall");
    }

    public static InstanceBuilder healing() {
        return new InstanceBuilder(InstanceType.HEALING);
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

    public InstanceBuilder min(float min) {
        this.min = min;
        return this;
    }

    public InstanceBuilder max(float max) {
        this.max = max;
        return this;
    }

    public InstanceBuilder value(WarlordsDamageHealingEvent event) {
        this.min = event.getMin();
        this.max = event.getMax();
        this.critChance = event.getCritChance();
        this.critMultiplier = event.getCritMultiplier();
        return this;
    }

    public InstanceBuilder value(Value.RangedValue rangedValue) {
        this.min = rangedValue.getMinValue();
        this.max = rangedValue.getMaxValue();
        return this;
    }

    public InstanceBuilder value(Value.SetValue setValue) {
        this.min = setValue.value().getCalculatedValue();
        this.max = setValue.value().getCalculatedValue();
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
        this.min = rangedValueCritable.getMinValue();
        this.max = rangedValueCritable.getMaxValue();
        this.critChance = rangedValueCritable.getCritChanceValue();
        this.critMultiplier = rangedValueCritable.getCritMultiplierValue();
        return this;
    }

    public InstanceBuilder critChance(float critChance) {
        this.critChance = critChance;
        return this;
    }

    public InstanceBuilder critMultiplier(float critMultiplier) {
        this.critMultiplier = critMultiplier;
        return this;
    }

    public InstanceBuilder showAsCrit(boolean showAsCrit) {
        if (showAsCrit) {
            this.critChance = 100;
        }
        return this;
    }

    public InstanceBuilder crit(Value.RangedValueCritable rangedValueCritable) {
        this.critChance = rangedValueCritable.getCritChanceValue();
        this.critMultiplier = rangedValueCritable.getCritMultiplierValue();
        return this;
    }

    public InstanceBuilder flags(InstanceFlags... flags) {
        this.flags.addAll(List.of(flags));
        return this;
    }

    public InstanceBuilder flags(EnumSet<InstanceFlags> flags) {
        this.flags = flags;
        return this;
    }

    public InstanceBuilder flag(InstanceFlags flag, boolean add) {
        if (add) {
            this.flags.add(flag);
        } else {
            this.flags.remove(flag);
        }
        return this;
    }

    public InstanceBuilder customFlags(CustomInstanceFlags... customFlags) {
        this.customFlags = List.of(customFlags);
        return this;
    }

    public InstanceBuilder customFlag(CustomInstanceFlags customFlag, boolean add) {
        if (add) {
            this.customFlags.add(customFlag);
        } else {
            this.customFlags.remove(customFlag);
        }
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

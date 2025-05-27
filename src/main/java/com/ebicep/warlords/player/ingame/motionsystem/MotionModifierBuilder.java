package com.ebicep.warlords.player.ingame.motionsystem;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.MotionAddon;

import java.util.ArrayList;
import java.util.List;

public class MotionModifierBuilder {

    private final List<MotionAddon> addons = new ArrayList<>();
    private WarlordsEntity from;
    private String name;
    private float modifier;
    private int duration;

    public MotionModifierBuilder setFrom(WarlordsEntity from) {
        this.from = from;
        return this;
    }

    public MotionModifierBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MotionModifierBuilder setModifier(float modifier) {
        this.modifier = modifier;
        return this;
    }

    public MotionModifierBuilder setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public MotionModifierBuilder linkToCooldown(WarlordsEntity warlordsEntity, AbstractCooldown<?> cooldown) {
        this.duration = -1;
        this.addons.add(new LinkedCooldownRemovalCondition(warlordsEntity, cooldown));
        return this;
    }

    public MotionModifierBuilder addAddons(MotionAddon... addon) {
        this.addons.addAll(List.of(addon));
        return this;
    }

    public MotionModifier build() {
        return new MotionModifier(from, name, modifier, duration, addons);
    }

}
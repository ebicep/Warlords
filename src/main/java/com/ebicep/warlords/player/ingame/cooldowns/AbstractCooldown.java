package com.ebicep.warlords.player.ingame.cooldowns;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.instances.DamageInstance;
import com.ebicep.warlords.player.ingame.cooldowns.instances.EnergyInstance;
import com.ebicep.warlords.player.ingame.cooldowns.instances.HealingInstance;

import java.util.function.Consumer;

public abstract class AbstractCooldown<T> implements DamageInstance, HealingInstance, EnergyInstance {

    protected String name;
    protected String nameAbbreviation;
    protected Class<T> cooldownClass;
    protected T cooldownObject;
    protected WarlordsEntity from;
    protected CooldownTypes cooldownType;
    protected Consumer<CooldownManager> onRemove;

    public AbstractCooldown(String name, String nameAbbreviation, Class<T> cooldownClass, T cooldownObject, WarlordsEntity from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove) {
        this.name = name;
        this.nameAbbreviation = nameAbbreviation;
        this.cooldownClass = cooldownClass;
        this.cooldownObject = cooldownObject;
        this.from = from;
        this.cooldownType = cooldownType;
        this.onRemove = onRemove;
    }

    public abstract String getNameAbbreviation();

    public abstract void onTick();

    public abstract boolean removeCheck();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getCooldownClass() {
        return cooldownClass;
    }

    public T getCooldownObject() {
        return cooldownObject;
    }

    public String getActionBarName() {
        return nameAbbreviation;
    }

    public void setNameAbbreviation(String nameAbbreviation) {
        this.nameAbbreviation = nameAbbreviation;
    }

    public WarlordsEntity getFrom() {
        return from;
    }

    public CooldownTypes getCooldownType() {
        return cooldownType;
    }

    public Consumer<CooldownManager> getOnRemove() {
        return onRemove;
    }
}

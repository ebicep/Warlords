package com.ebicep.warlords.player.cooldowns;

import com.ebicep.warlords.player.WarlordsPlayer;

import java.util.function.Consumer;

public abstract class AbstractCooldown<T> implements DamageHealingInstance {

    protected String name;
    protected String nameAbbreviation;
    protected Class<T> cooldownClass;
    protected T cooldownObject;
    protected WarlordsPlayer from;
    protected CooldownTypes cooldownType;
    protected Consumer<CooldownManager> onRemove;

    public AbstractCooldown(String name, String nameAbbreviation, Class<T> cooldownClass, T cooldownObject, WarlordsPlayer from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove) {
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

    public WarlordsPlayer getFrom() {
        return from;
    }

    public CooldownTypes getCooldownType() {
        return cooldownType;
    }

    public Consumer<CooldownManager> getOnRemove() {
        return onRemove;
    }
}

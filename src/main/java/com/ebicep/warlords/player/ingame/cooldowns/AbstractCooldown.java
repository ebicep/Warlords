package com.ebicep.warlords.player.ingame.cooldowns;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.instances.DamageInstance;
import com.ebicep.warlords.player.ingame.cooldowns.instances.EnergyInstance;
import com.ebicep.warlords.player.ingame.cooldowns.instances.HealingInstance;
import com.ebicep.warlords.player.ingame.cooldowns.instances.KnockbackInstance;

import java.util.function.Consumer;

public abstract class AbstractCooldown<T> implements DamageInstance, HealingInstance, EnergyInstance, KnockbackInstance {

    protected String name;
    protected String nameAbbreviation;
    protected Class<T> cooldownClass;
    protected T cooldownObject;
    protected WarlordsEntity from;
    protected CooldownTypes cooldownType;
    protected Consumer<CooldownManager> onRemove;
    protected Consumer<CooldownManager> onRemoveForce;
    protected boolean removeOnDeath = true;

    public AbstractCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove
    ) {
        this.name = name;
        this.nameAbbreviation = nameAbbreviation;
        this.cooldownClass = cooldownClass;
        this.cooldownObject = cooldownObject;
        this.from = from;
        this.cooldownType = cooldownType;
        this.onRemove = onRemove;
    }

    public AbstractCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce
    ) {
        this.name = name;
        this.nameAbbreviation = nameAbbreviation;
        this.cooldownClass = cooldownClass;
        this.cooldownObject = cooldownObject;
        this.from = from;
        this.cooldownType = cooldownType;
        this.onRemove = onRemove;
        this.onRemoveForce = onRemoveForce;
    }

    public AbstractCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            boolean removeOnDeath
    ) {
        this.name = name;
        this.nameAbbreviation = nameAbbreviation;
        this.cooldownClass = cooldownClass;
        this.cooldownObject = cooldownObject;
        this.from = from;
        this.cooldownType = cooldownType;
        this.onRemove = onRemove;
        this.removeOnDeath = removeOnDeath;
    }

    public AbstractCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove,
            Consumer<CooldownManager> onRemoveForce,
            boolean removeOnDeath
    ) {
        this.name = name;
        this.nameAbbreviation = nameAbbreviation;
        this.cooldownClass = cooldownClass;
        this.cooldownObject = cooldownObject;
        this.from = from;
        this.cooldownType = cooldownType;
        this.onRemove = onRemove;
        this.onRemoveForce = onRemoveForce;
        this.removeOnDeath = removeOnDeath;
    }

    public abstract String getNameAbbreviation();

    public void setNameAbbreviation(String nameAbbreviation) {
        this.nameAbbreviation = nameAbbreviation;
    }

    public abstract void onTick(WarlordsEntity from);

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

    public WarlordsEntity getFrom() {
        return from;
    }

    public CooldownTypes getCooldownType() {
        return cooldownType;
    }

    public Consumer<CooldownManager> getOnRemove() {
        return onRemove;
    }

    public void setOnRemove(Consumer<CooldownManager> onRemove) {
        this.onRemove = onRemove;
    }

    public boolean isRemoveOnDeath() {
        return removeOnDeath;
    }

    public void setRemoveOnDeath(boolean removeOnDeath) {
        this.removeOnDeath = removeOnDeath;
    }

    public Consumer<CooldownManager> getOnRemoveForce() {
        return onRemoveForce;
    }

    public void setOnRemoveForce(Consumer<CooldownManager> onRemoveForce) {
        this.onRemoveForce = onRemoveForce;
    }
}

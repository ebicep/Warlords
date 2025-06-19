package com.ebicep.warlords.player.ingame.cooldowns;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.type.*;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractCooldown<T> implements DamageInstance, HealingInstance, EnergyInstance, PlayerNameInstance, SpecDamageReductionInstance, DebugInstance {

    public static List<AbstractCooldown<?>> COOLDOWNS_WITH_LISTENERS = new ArrayList<>();
    protected String name;
    protected String nameAbbreviation;
    protected Class<T> cooldownClass;
    protected T cooldownObject;
    protected WarlordsEntity from;
    protected CooldownTypes cooldownType;
    protected Consumer<CooldownManager> onRemove;
    protected Consumer<CooldownManager> onRemoveForce;
    protected boolean removeOnDeath;
    private final Listener activeListener;
    private List<DamageInstance> extraDamageInstances = null;
    private List<HealingInstance> extraHealingInstances = null;
    private List<CooldownFlag> flags = new ArrayList<>();
    private boolean markedForRemoval = false;

    public AbstractCooldown(
            String name,
            String nameAbbreviation,
            Class<T> cooldownClass,
            T cooldownObject,
            WarlordsEntity from,
            CooldownTypes cooldownType,
            Consumer<CooldownManager> onRemove
    ) {
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, true);
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
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, cooldownManager -> {}, removeOnDeath);
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
        if (name == null) {
            ChatUtils.MessageType.GAME.sendErrorMessage(new Throwable("null cooldown name"));
        }
        this.name = name == null ? "UNKNOWN" : name;
        this.nameAbbreviation = nameAbbreviation;
        this.cooldownClass = cooldownClass;
        this.cooldownObject = cooldownObject;
        this.from = from;
        this.cooldownType = cooldownType;
        this.onRemove = onRemove;
        this.removeOnDeath = removeOnDeath;
        this.activeListener = getListener();
        if (activeListener != null) {
            COOLDOWNS_WITH_LISTENERS.add(this);
            from.getGame().registerEvents(activeListener);
            this.onRemoveForce = cooldownManager -> {
                COOLDOWNS_WITH_LISTENERS.remove(this);
                HandlerList.unregisterAll(activeListener);
                onRemoveForce.accept(cooldownManager);
                if (changesPlayerName()) {
                    cooldownManager.queueUpdatePlayerNames();
                }
            };
        } else {
            this.onRemoveForce = cooldownManager -> {
                onRemoveForce.accept(cooldownManager);
                if (changesPlayerName()) {
                    cooldownManager.queueUpdatePlayerNames();
                }
            };
        }
    }

    protected Listener getListener() {
        return null;
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
        this(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove, onRemoveForce, true);
    }

    @Override
    public @Nullable List<DamageInstance> getExtraDamageInstances() {
        return extraDamageInstances;
    }

    @Override
    public @Nullable List<HealingInstance> getExtraHealingInstances() {
        return extraHealingInstances;
    }

    public void expire(CooldownManager cooldownManager) {
        setMarkedForRemoval(true);
        getOnRemove().accept(cooldownManager);
        getOnRemoveForce().accept(cooldownManager);
        cooldownManager.updatePlayerNames(this);
    }

    public Consumer<CooldownManager> getOnRemove() {
        return onRemove;
    }

    public void setOnRemove(Consumer<CooldownManager> onRemove) {
        this.onRemove = onRemove;
    }

    public Consumer<CooldownManager> getOnRemoveForce() {
        return onRemoveForce;
    }

    public void setOnRemoveForce(Consumer<CooldownManager> onRemoveForce) {
        this.onRemoveForce = onRemoveForce;
    }

    public abstract Component getNameAbbreviation();

    public void setNameAbbreviation(String nameAbbreviation) {
        this.nameAbbreviation = nameAbbreviation;
    }

    public abstract void onTick(WarlordsEntity from);

    public abstract boolean removeCheck();

    public TextColor customActionBarColor() {
        return null;
    }

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

    public void setCooldownType(CooldownTypes cooldownType) {
        this.cooldownType = cooldownType;
    }

    public boolean isRemoveOnDeath() {
        return removeOnDeath;
    }

    public void setRemoveOnDeath(boolean removeOnDeath) {
        this.removeOnDeath = removeOnDeath;
    }

    public void addExtraDamageInstance(DamageInstance extraDamageInstance) {
        if (extraDamageInstances == null) {
            extraDamageInstances = new ArrayList<>();
        }
        extraDamageInstances.add(extraDamageInstance);
    }

    public Listener getActiveListener() {
        return activeListener;
    }

    public List<CooldownFlag> getFlags() {
        return flags;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void setMarkedForRemoval(boolean markedForRemoval) {
        this.markedForRemoval = markedForRemoval;
    }

}

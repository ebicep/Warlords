package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class WarlordsDamageHealingEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final InstanceBuilder.InstanceType instanceType;

    private WarlordsEntity source;
    @Nullable
    private AbstractAbility ability;
    private String cause;
    private float min;
    private float max;
    private float critChance;
    private float critMultiplier;
    private final EnumSet<InstanceFlags> flags;
    private final List<CustomInstanceFlags> customFlags;
    @Nullable
    private final UUID uuid;
    private boolean cancelled;

    public WarlordsDamageHealingEvent(
            WarlordsEntity player,
            WarlordsEntity source,
            String cause,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean instanceType,
            EnumSet<InstanceFlags> flags,
            List<CustomInstanceFlags> customFlags
    ) {
        this(player, source, cause, min, max, critChance, critMultiplier, instanceType, flags, customFlags, null);
    }

    public WarlordsDamageHealingEvent(
            WarlordsEntity player,
            WarlordsEntity source,
            String cause,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean instanceType,
            EnumSet<InstanceFlags> flags,
            List<CustomInstanceFlags> customFlags,
            @Nullable UUID uuid
    ) {
        super(player);
        this.source = source;
        this.cause = cause;
        this.min = min;
        this.max = max;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.instanceType = instanceType ? InstanceBuilder.InstanceType.DAMAGE : InstanceBuilder.InstanceType.HEALING;
        this.flags = flags;
        this.customFlags = customFlags;
        this.uuid = uuid;
    }

    public WarlordsDamageHealingEvent(
            InstanceBuilder.InstanceType instanceType,
            @Nonnull WarlordsEntity player,
            WarlordsEntity source,
            @Nullable AbstractAbility ability,
            String cause,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            EnumSet<InstanceFlags> flags,
            List<CustomInstanceFlags> customFlags,
            @Nullable UUID uuid
    ) {
        super(player);
        this.instanceType = instanceType;
        this.source = source;
        this.ability = ability;
        this.cause = cause;
        this.flags = flags;
        this.min = min;
        this.max = max;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.customFlags = customFlags;
        this.uuid = uuid;
    }

    public WarlordsEntity getSource() {
        return source;
    }

    public void setSource(WarlordsEntity source) {
        this.source = source;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        if (flags.contains(InstanceFlags.TRUE_DAMAGE)) {
            return;
        }
        this.min = min;
    }

    public void setMinForce(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        if (flags.contains(InstanceFlags.TRUE_DAMAGE)) {
            return;
        }
        this.max = max;
    }

    public void setMaxForce(float max) {
        this.max = max;
    }

    public float getCritChance() {
        return critChance;
    }

    public void setCritChance(float critChance) {
        this.critChance = critChance;
    }

    public float getCritMultiplier() {
        return critMultiplier;
    }

    public void setCritMultiplier(float critMultiplier) {
        this.critMultiplier = critMultiplier;
    }

    public boolean isDamageInstance() {
        return instanceType == InstanceBuilder.InstanceType.DAMAGE;
    }

    public boolean isHealingInstance() {
        return instanceType == InstanceBuilder.InstanceType.HEALING;
    }

    public InstanceBuilder.InstanceType getInstanceType() {
        return instanceType;
    }

    public EnumSet<InstanceFlags> getFlags() {
        return flags;
    }

    public List<CustomInstanceFlags> getCustomFlags() {
        return customFlags;
    }

    @Nullable
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public String toString() {
        return "WarlordsDamageHealingEvent{" +
                "player=" + getWarlordsEntity() +
                ", attacker=" + source +
                ", ability=" + cause +
                ", min=" + min +
                ", max=" + max +
                ", critChance=" + critChance +
                ", critMultiplier=" + critMultiplier +
                ", isDamageInstance=" + instanceType +
                ", cancelled=" + cancelled +
                '}';
    }

}

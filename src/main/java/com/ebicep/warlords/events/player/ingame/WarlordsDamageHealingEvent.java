
package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.instances.CustomInstanceFlags;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
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

    private WarlordsEntity attacker;
    private String ability;
    private float min;
    private float max;
    private float critChance;
    private float critMultiplier;
    private boolean isDamageInstance;

    private final EnumSet<InstanceFlags> flags;
    private final List<CustomInstanceFlags> customFlags;
    @Nullable
    private final UUID uuid;
    private boolean cancelled;

    public WarlordsDamageHealingEvent(
            WarlordsEntity player,
            WarlordsEntity attacker,
            String ability,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean isDamageInstance,
            EnumSet<InstanceFlags> flags,
            List<CustomInstanceFlags> customFlags,
            @Nullable UUID uuid
    ) {
        super(player);
        this.attacker = attacker;
        this.ability = ability;
        this.min = min;
        this.max = max;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.isDamageInstance = isDamageInstance;
        this.flags = flags;
        this.customFlags = customFlags;
        this.uuid = uuid;
    }

    public WarlordsDamageHealingEvent(
            WarlordsEntity player,
            WarlordsEntity attacker,
            String ability,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean isDamageInstance,
            EnumSet<InstanceFlags> flags,
            List<CustomInstanceFlags> customFlags
    ) {
        this(player, attacker, ability, min, max, critChance, critMultiplier, isDamageInstance, flags, customFlags, null);
    }

    public WarlordsEntity getAttacker() {
        return attacker;
    }

    public void setAttacker(WarlordsEntity attacker) {
        this.attacker = attacker;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
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
        return isDamageInstance;
    }

    public void setIsDamageInstance(boolean isDamageInstance) {
        this.isDamageInstance = isDamageInstance;
    }

    public boolean isHealingInstance() {
        return !isDamageInstance;
    }

    public void setIsHealingInstance(boolean isHealingInstance) {
        this.isDamageInstance = !isHealingInstance;
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public String toString() {
        return "WarlordsDamageHealingEvent{" +
                "player=" + getWarlordsEntity() +
                ", attacker=" + attacker +
                ", ability=" + ability +
                ", min=" + min +
                ", max=" + max +
                ", critChance=" + critChance +
                ", critMultiplier=" + critMultiplier +
                ", isDamageInstance=" + isDamageInstance +
                ", cancelled=" + cancelled +
                '}';
    }
            
}

package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 *
 */
public class WarlordsDamageHealingEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final InstanceBuilder.InstanceType instanceType;
    private final EnumSet<InstanceFlags> flags;
    private final List<CustomInstanceFlags> customFlags;
    @Nullable
    private final UUID uuid;
    private WarlordsEntity source;
    @Nullable
    private final AbstractAbility ability;
    private final String cause;
    private final FloatModifiable min;
    private final FloatModifiable max;
    private final FloatModifiable critChance;
    private final FloatModifiable critMultiplier;
    private final List<TextComponent> debugMessages;
    private boolean cancelled;

    public WarlordsDamageHealingEvent(
            WarlordsEntity player,
            WarlordsEntity source,
            String cause,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean instanceTypeDamage,
            EnumSet<InstanceFlags> flags,
            List<CustomInstanceFlags> customFlags,
            List<TextComponent> debugMessages
    ) {
        this(instanceTypeDamage ? InstanceBuilder.InstanceType.DAMAGE : InstanceBuilder.InstanceType.HEALING,
                player, source, null, cause,
                min, max, critChance, critMultiplier,
                flags, customFlags, debugMessages, null
        );
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
            List<TextComponent> debugMessages,
            @Nullable UUID uuid
    ) {
        super(player);
        this.instanceType = instanceType;
        this.source = source;
        this.ability = ability;
        this.cause = cause;
        this.flags = flags;
        this.min = new FloatModifiable(min);
        this.max = new FloatModifiable(max);
        this.critChance = new FloatModifiable(critChance);
        this.critMultiplier = new FloatModifiable(critMultiplier);
        this.customFlags = customFlags;
        this.debugMessages = debugMessages;
        this.uuid = uuid;
    }

    public WarlordsDamageHealingEvent(
            WarlordsEntity player,
            WarlordsEntity source,
            String cause,
            float min,
            float max,
            float critChance,
            float critMultiplier,
            boolean instanceTypeDamage,
            EnumSet<InstanceFlags> flags,
            List<CustomInstanceFlags> customFlags,
            List<TextComponent> debugMessages,
            @Nullable UUID uuid
    ) {
        this(instanceTypeDamage ? InstanceBuilder.InstanceType.DAMAGE : InstanceBuilder.InstanceType.HEALING,
                player, source, null, cause,
                min, max, critChance, critMultiplier,
                flags, customFlags, debugMessages, uuid
        );
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

    public WarlordsEntity getSource() {
        return source;
    }

    public void setSource(WarlordsEntity source) {
        this.source = source;
    }

    @Nullable
    public AbstractAbility getAbility() {
        return ability;
    }

    public String getCause() {
        return cause;
    }

    public void applyToMinMax(Consumer<FloatModifiable> consumer) {
        consumer.accept(min);
        consumer.accept(max);
    }

    public FloatModifiable getMin() {
        return min;
    }

    public FloatModifiable getMax() {
        return max;
    }

    public FloatModifiable getCritChance() {
        return critChance;
    }

    public FloatModifiable getCritMultiplier() {
        return critMultiplier;
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

    public List<TextComponent> getDebugMessages() {
        return debugMessages;
    }

    @Nullable
    public UUID getUUID() {
        return uuid;
    }

}

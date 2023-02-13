package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.Set;

public class WarlordsBlueAbilityTargetEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Set<WarlordsEntity> targets;

    public WarlordsBlueAbilityTargetEvent(@Nonnull WarlordsEntity player, WarlordsEntity... targets) {
        super(player);
        this.targets = Set.of(targets);
    }

    public WarlordsBlueAbilityTargetEvent(@Nonnull WarlordsEntity player, Set<WarlordsEntity> targets) {
        super(player);
        this.targets = targets;
    }

    public Set<WarlordsEntity> getTargets() {
        return targets;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

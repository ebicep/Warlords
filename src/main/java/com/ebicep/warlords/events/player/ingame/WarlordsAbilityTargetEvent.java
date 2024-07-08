package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.Set;

public class WarlordsAbilityTargetEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final String abilityName;
    private final Set<WarlordsEntity> targets;

    public WarlordsAbilityTargetEvent(@Nonnull WarlordsEntity player, String abilityName, WarlordsEntity... targets) {
        super(player);
        this.abilityName = abilityName;
        this.targets = Set.of(targets);
    }

    public WarlordsAbilityTargetEvent(@Nonnull WarlordsEntity player, String abilityName, Set<WarlordsEntity> targets) {
        super(player);
        this.abilityName = abilityName;
        this.targets = targets;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public Set<WarlordsEntity> getTargets() {
        return targets;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }


    public static class WarlordsBlueAbilityTargetEvent extends WarlordsAbilityTargetEvent {
        public WarlordsBlueAbilityTargetEvent(@Nonnull WarlordsEntity player, String abilityName, WarlordsEntity... targets) {
            super(player, abilityName, targets);
        }

        public WarlordsBlueAbilityTargetEvent(@Nonnull WarlordsEntity player, String abilityName, Set<WarlordsEntity> targets) {
            super(player, abilityName, targets);
        }
    }
}

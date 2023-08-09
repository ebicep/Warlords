package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsUndyingArmyPopEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final UndyingArmy undyingArmy;

    public WarlordsUndyingArmyPopEvent(@Nonnull WarlordsEntity player, UndyingArmy undyingArmy) {
        super(player);
        this.undyingArmy = undyingArmy;
    }

    public UndyingArmy getUndyingArmy() {
        return undyingArmy;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}

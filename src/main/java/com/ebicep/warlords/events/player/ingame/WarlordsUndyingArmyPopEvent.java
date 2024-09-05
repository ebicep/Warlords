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

    private final UndyingArmy.UndyingArmyData undyingArmyData;

    public WarlordsUndyingArmyPopEvent(@Nonnull WarlordsEntity player, UndyingArmy.UndyingArmyData data) {
        super(player);
        this.undyingArmyData = data;
    }

    public UndyingArmy.UndyingArmyData getUndyingArmyData() {
        return undyingArmyData;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}

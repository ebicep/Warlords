package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsHammerToCrownEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }
    private final AbstractCooldown<?> cooldown;

    public WarlordsHammerToCrownEvent(@Nonnull WarlordsEntity player, @Nonnull AbstractCooldown<?> cooldown) {
        super(player);
        this.cooldown = cooldown;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public AbstractCooldown<?> getCooldown() {
        return cooldown;
    }

}
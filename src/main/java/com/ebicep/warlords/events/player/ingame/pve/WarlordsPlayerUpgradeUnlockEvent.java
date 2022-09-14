package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsPlayerEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsPlayerUpgradeUnlockEvent extends AbstractWarlordsPlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Upgrade upgrade;

    public WarlordsPlayerUpgradeUnlockEvent(@Nonnull WarlordsEntity player, Upgrade upgrade) {
        super(player);
        this.upgrade = upgrade;
    }

    public Upgrade getUpgrade() {
        return upgrade;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

package com.ebicep.warlords.pve.bountysystem.events;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BountyStartEvent extends AbstractBountyEvent {

    private static final HandlerList handlers = new HandlerList();

    public BountyStartEvent(DatabasePlayer databasePlayer, AbstractBounty bounty) {
        super(databasePlayer, bounty);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

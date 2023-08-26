package com.ebicep.warlords.pve.bountysystem.events;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import org.bukkit.event.Event;

public abstract class AbstractBountyEvent extends Event {

    protected final DatabasePlayer databasePlayer;
    protected final AbstractBounty bounty;

    protected AbstractBountyEvent(DatabasePlayer databasePlayer, AbstractBounty bounty) {
        this.databasePlayer = databasePlayer;
        this.bounty = bounty;
    }

    public DatabasePlayer getDatabasePlayer() {
        return databasePlayer;
    }

    public AbstractBounty getBounty() {
        return bounty;
    }

}

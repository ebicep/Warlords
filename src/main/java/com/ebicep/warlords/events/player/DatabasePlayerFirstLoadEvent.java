package com.ebicep.warlords.events.player;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DatabasePlayerFirstLoadEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final DatabasePlayer databasePlayer;

    public DatabasePlayerFirstLoadEvent(Player player, DatabasePlayer databasePlayer) {
        super(player);
        this.databasePlayer = databasePlayer;
    }

    public DatabasePlayer getDatabasePlayer() {
        return databasePlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

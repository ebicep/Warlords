package com.ebicep.warlords.pve.weapons.events;

import com.ebicep.warlords.pve.StarPieces;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class StarPieceSynthesizedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final StarPieces starPiece;

    public StarPieceSynthesizedEvent(UUID uuid, StarPieces starPiece) {
        this.uuid = uuid;
        this.starPiece = starPiece;
    }

    public UUID getUUID() {
        return uuid;
    }

    public StarPieces getStarPiece() {
        return starPiece;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

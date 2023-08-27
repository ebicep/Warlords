package com.ebicep.warlords.events.player;

import com.ebicep.warlords.player.general.Specializations;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class SpecPrestigeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final Specializations spec;
    private final int prestige;

    public SpecPrestigeEvent(UUID uuid, Specializations spec, int prestige) {
        this.uuid = uuid;
        this.spec = spec;
        this.prestige = prestige;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Specializations getSpec() {
        return spec;
    }

    public int getPrestige() {
        return prestige;
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

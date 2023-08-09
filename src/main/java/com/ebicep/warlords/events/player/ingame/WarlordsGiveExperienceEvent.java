package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

public class WarlordsGiveExperienceEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final LinkedHashMap<String, Long> experienceSummary;

    public WarlordsGiveExperienceEvent(@Nonnull WarlordsEntity player, LinkedHashMap<String, Long> experienceSummary) {
        super(player);
        this.experienceSummary = experienceSummary;
    }

    public LinkedHashMap<String, Long> getExperienceSummary() {
        return experienceSummary;
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

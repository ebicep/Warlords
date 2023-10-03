package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;

public class WarlordsUpgradeTreeBuilderAddUpgradeEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();

    private final UpgradeTreeBuilder builder;
    private final AtomicReference<Float> value;

    public WarlordsUpgradeTreeBuilderAddUpgradeEvent(@Nonnull WarlordsEntity player, UpgradeTreeBuilder builder, AtomicReference<Float> value) {
        super(player);
        this.builder = builder;
        this.value = value;
    }

    public UpgradeTreeBuilder getBuilder() {
        return builder;
    }

    public AtomicReference<Float> getValue() {
        return value;
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

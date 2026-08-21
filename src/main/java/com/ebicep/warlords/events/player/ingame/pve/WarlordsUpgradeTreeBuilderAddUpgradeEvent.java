package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class WarlordsUpgradeTreeBuilderAddUpgradeEvent extends AbstractWarlordsEntityEvent {
    private static final HandlerList handlers = new HandlerList();

    private final UpgradeTreeBuilder builder;
    private final FloatModifiable value;

    public WarlordsUpgradeTreeBuilderAddUpgradeEvent(@Nonnull WarlordsEntity player, UpgradeTreeBuilder builder, FloatModifiable value) {
        super(player);
        this.builder = builder;
        this.value = value;
    }

    public UpgradeTreeBuilder getBuilder() {
        return builder;
    }

    public FloatModifiable getValue() {
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

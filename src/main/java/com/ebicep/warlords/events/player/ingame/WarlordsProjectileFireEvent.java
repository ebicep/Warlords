package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.List;

public class WarlordsProjectileFireEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final AbstractAbility ability;
    private final List<? extends AbstractPiercingProjectile<?, ?>.InternalProjectile> internalProjectiles;

    public WarlordsProjectileFireEvent(
            @Nonnull WarlordsEntity player,
            AbstractAbility ability,
            List<? extends AbstractPiercingProjectile<?, ?>.InternalProjectile> internalProjectiles
    ) {
        super(player);
        this.ability = ability;
        this.internalProjectiles = internalProjectiles;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public AbstractAbility getAbility() {
        return ability;
    }

    public List<? extends AbstractPiercingProjectile<?, ?>.InternalProjectile> getInternalProjectiles() {
        return internalProjectiles;
    }

}

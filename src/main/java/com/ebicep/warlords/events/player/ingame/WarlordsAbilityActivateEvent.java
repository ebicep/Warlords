package com.ebicep.warlords.events.player.ingame;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public abstract class WarlordsAbilityActivateEvent extends AbstractWarlordsEntityEvent implements Cancellable {

    private final Player player;
    private final AbstractAbility ability;
    private final int slot;
    private boolean cancelled;

    public WarlordsAbilityActivateEvent(@Nonnull WarlordsEntity warlordsEntity, Player player, AbstractAbility ability, int slot) {
        super(warlordsEntity);
        this.player = player;
        this.ability = ability;
        this.slot = slot;
    }

    public Player getPlayer() {
        return player;
    }

    public AbstractAbility getAbility() {
        return ability;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static class Pre extends WarlordsAbilityActivateEvent {

        private static final HandlerList handlers = new HandlerList();

        public Pre(@Nonnull WarlordsEntity warlordsEntity, Player player, AbstractAbility ability, int slot) {
            super(warlordsEntity, player, ability, slot);
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

    public static class Post extends WarlordsAbilityActivateEvent {

        private static final HandlerList handlers = new HandlerList();

        public Post(@Nonnull WarlordsEntity warlordsEntity, Player player, AbstractAbility ability, int slot) {
            super(warlordsEntity, player, ability, slot);
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
}

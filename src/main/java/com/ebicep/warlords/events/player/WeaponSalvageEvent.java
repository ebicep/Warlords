package com.ebicep.warlords.events.player;

import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class WeaponSalvageEvent extends Event {

    protected final UUID uuid;
    protected final AbstractWeapon weapon;
    protected final AtomicInteger salvageAmount;

    protected WeaponSalvageEvent(UUID uuid, AbstractWeapon weapon, AtomicInteger salvageAmount) {
        this.uuid = uuid;
        this.weapon = weapon;
        this.salvageAmount = salvageAmount;
    }

    public UUID getUUID() {
        return uuid;
    }

    public AbstractWeapon getWeapon() {
        return weapon;
    }

    public AtomicInteger getSalvageAmount() {
        return salvageAmount;
    }

    public static class Pre extends WeaponSalvageEvent {

        private static final HandlerList handlers = new HandlerList();

        public static HandlerList getHandlerList() {
            return handlers;
        }

        public Pre(UUID uuid, AbstractWeapon weapon, AtomicInteger salvageAmount) {
            super(uuid, weapon, salvageAmount);
        }

        @Nonnull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }
    }

    public static class Post extends WeaponSalvageEvent {

        private static final HandlerList handlers = new HandlerList();

        public static HandlerList getHandlerList() {
            return handlers;
        }

        public Post(UUID uuid, AbstractWeapon weapon, AtomicInteger salvageAmount) {
            super(uuid, weapon, salvageAmount);
        }

        @Nonnull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }
    }
}

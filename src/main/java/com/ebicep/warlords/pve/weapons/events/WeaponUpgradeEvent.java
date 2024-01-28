package com.ebicep.warlords.pve.weapons.events;

import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class WeaponUpgradeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final AbstractWeapon weapon;

    public WeaponUpgradeEvent(UUID uuid, AbstractWeapon weapon) {
        this.uuid = uuid;
        this.weapon = weapon;
    }

    public UUID getUUID() {
        return uuid;
    }

    public AbstractWeapon getWeapon() {
        return weapon;
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

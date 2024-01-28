package com.ebicep.warlords.pve.weapons.events;

import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class LegendaryWeaponCraftEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final AbstractLegendaryWeapon abstractLegendaryWeapon;

    public LegendaryWeaponCraftEvent(UUID uuid, AbstractLegendaryWeapon abstractLegendaryWeapon) {
        this.uuid = uuid;
        this.abstractLegendaryWeapon = abstractLegendaryWeapon;
    }

    public UUID getUUID() {
        return uuid;
    }

    public AbstractLegendaryWeapon getAbstractLegendaryWeapon() {
        return abstractLegendaryWeapon;
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

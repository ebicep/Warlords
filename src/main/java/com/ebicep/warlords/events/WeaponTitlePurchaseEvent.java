package com.ebicep.warlords.events;

import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.UUID;

public class WeaponTitlePurchaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final AbstractLegendaryWeapon abstractLegendaryWeapon;
    private final LegendaryTitles title;


    public WeaponTitlePurchaseEvent(UUID uuid, AbstractLegendaryWeapon abstractLegendaryWeapon, LegendaryTitles title) {
        this.uuid = uuid;
        this.abstractLegendaryWeapon = abstractLegendaryWeapon;
        this.title = title;
    }

    public UUID getUUID() {
        return uuid;
    }

    public AbstractLegendaryWeapon getAbstractLegendaryWeapon() {
        return abstractLegendaryWeapon;
    }

    public LegendaryTitles getTitle() {
        return title;
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

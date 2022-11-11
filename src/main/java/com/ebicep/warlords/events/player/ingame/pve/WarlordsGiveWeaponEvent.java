package com.ebicep.warlords.events.player.ingame.pve;

import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.bukkit.event.HandlerList;

public class WarlordsGiveWeaponEvent extends AbstractWarlordsEntityEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AbstractWeapon weapon;

    public WarlordsGiveWeaponEvent(WarlordsEntity player, AbstractWeapon weapon) {
        super(player);
        this.weapon = weapon;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public AbstractWeapon getWeapon() {
        return weapon;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

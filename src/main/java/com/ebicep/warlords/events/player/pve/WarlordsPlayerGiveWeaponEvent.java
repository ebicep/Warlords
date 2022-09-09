package com.ebicep.warlords.events.player.pve;

import com.ebicep.warlords.events.player.AbstractWarlordsPlayerEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.bukkit.event.HandlerList;

public class WarlordsPlayerGiveWeaponEvent extends AbstractWarlordsPlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final AbstractWeapon weapon;

    public WarlordsPlayerGiveWeaponEvent(WarlordsEntity player, AbstractWeapon weapon) {
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

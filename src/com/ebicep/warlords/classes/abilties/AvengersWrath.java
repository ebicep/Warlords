package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.event.player.PlayerInteractEvent;

public class AvengersWrath extends AbstractAbility {

    public AvengersWrath() {
        super("Avenger's Wrath", 0, 0, 53, 0, 0, 0, "avengers wrath description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Warlords.getPlayer(e.getPlayer()).setWrath(12);
    }
}

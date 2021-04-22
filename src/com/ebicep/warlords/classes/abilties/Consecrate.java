package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.event.player.PlayerInteractEvent;

public class Consecrate extends AbstractAbility {
    public Consecrate() {
        super("Consecrate", 8, 50, 20, 175, "consecrate description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {

    }
}

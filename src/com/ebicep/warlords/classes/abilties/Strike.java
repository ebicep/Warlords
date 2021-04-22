package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.event.player.PlayerInteractEvent;

public class Strike extends AbstractAbility {

    public Strike(String name, int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        super(name, cooldown, energyCost, critChance, critMultiplier, description);
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {

    }
}

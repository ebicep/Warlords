package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.event.player.PlayerInteractEvent;

public class LightInfusion extends AbstractAbility {


    public LightInfusion(int cooldown, String description) {
        super("Light Infusion", cooldown, 0, 0, 0, description);
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {

    }
}

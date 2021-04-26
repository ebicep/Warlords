package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.event.player.PlayerInteractEvent;

public class OrbsOfLife extends AbstractAbility {

    public OrbsOfLife(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {

    }
}

package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.event.player.PlayerInteractEvent;

public class Consecrate extends AbstractAbility {
    public Consecrate() {
        super("Consecrate", -158, -214, 8, 50, 20, 175, "consecrate description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        ConsecrateCircle consecrateCircle = new ConsecrateCircle(e.getPlayer(), e.getPlayer().getLocation(), 5, 5, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        consecrateCircle.spawn();
        Warlords.getPlayer(e.getPlayer()).subtractEnergy(energyCost);
        Warlords.consecrates.add(consecrateCircle);
    }
}

package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;

public class HammerOfLight extends AbstractAbility {
    public HammerOfLight() {
        super("Hammer of Light", 119, 158, 60 + 11, 30, 20, 175, "hammer description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        ConsecrateHammerCircle consecrateHammerCircle = new ConsecrateHammerCircle(e.getPlayer(), e.getPlayer().getTargetBlock((HashSet<Byte>) null, 15).getLocation(), 5, 8, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        consecrateHammerCircle.spawn();
        Warlords.getPlayer(e.getPlayer()).subtractEnergy(energyCost);
        Warlords.consecrates.add(consecrateHammerCircle);
    }
}
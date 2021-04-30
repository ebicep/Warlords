package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.event.player.PlayerInteractEvent;

public class Consecrate extends AbstractAbility {
    public Consecrate(int minDamageHeal, int maxDamageHeal, int energyCost, int critChance, int critMultiplier) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 8, energyCost, critChance, critMultiplier, "consecrate description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        DamageHealCircle damageHealCircle = new DamageHealCircle(e.getPlayer(), e.getPlayer().getLocation(), 5, 5, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawn();
        Warlords.getPlayer(e.getPlayer()).subtractEnergy(energyCost);
        Warlords.damageHealCircles.add(damageHealCircle);
    }
}

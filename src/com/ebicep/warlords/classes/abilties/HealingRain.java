package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;

public class HealingRain extends AbstractAbility {

    public HealingRain() {
        super("Healing Rain", 170, 230, 53, 50, 15, 200, "healing rain description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        DamageHealCircle damageHealCircle = new DamageHealCircle(e.getPlayer(), e.getPlayer().getTargetBlock((HashSet<Byte>) null, 15).getLocation(), 5, 10, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawn();
        Warlords.getPlayer(e.getPlayer()).subtractEnergy(energyCost);
        Warlords.damageHealCircles.add(damageHealCircle);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "mage.healingrain.impact", 1, 1);
        }
    }
}

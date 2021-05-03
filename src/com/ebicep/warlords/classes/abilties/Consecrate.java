package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Consecrate extends AbstractAbility {
    public Consecrate(int minDamageHeal, int maxDamageHeal, int energyCost, int critChance, int critMultiplier) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 8, energyCost, critChance, critMultiplier, "consecrate description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        DamageHealCircle damageHealCircle = new DamageHealCircle(e.getPlayer(), e.getPlayer().getLocation(), 5, 5, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawn();
        Warlords.getPlayer(e.getPlayer()).subtractEnergy(energyCost);
        Warlords.damageHealCircles.add(damageHealCircle);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "paladin.consecrate.activation", 1, 1);
        }
    }
}

package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Consecrate extends AbstractAbility {
    public Consecrate(int minDamageHeal, int maxDamageHeal, int energyCost, int critChance, int critMultiplier) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 8, energyCost, critChance, critMultiplier,
                "§7Consecrate the ground below your\n" +
                "§7feet, declaring it sacred. Enemies\n" +
                "§7standing on it will take §c%dynamic.value% §7-\n" +
                "§c%dynamic.value% §7damage per second and\n" +
                "§7take §c%dynamic.value% §7increased damage from\n" +
                "§7your paladin strikes. Lasts §65\n" +
                "§7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        DamageHealCircle damageHealCircle = new DamageHealCircle(player, player.getLocation(), 5, 5, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawn();
        Warlords.getPlayer(player).subtractEnergy(energyCost);
        Warlords.damageHealCircles.add(damageHealCircle);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.consecrate.activation", 1, 1);
        }
    }
}

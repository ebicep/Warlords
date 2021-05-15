package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.ActionBarStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;

public class HealingRain extends AbstractAbility {

    public HealingRain() {
        super("Healing Rain", 170, 230, 53, 50, 15, 200,
                "§7Conjure rain at targeted\n" +
                        "§7location that will restore §a170\n" +
                        "§7- §a230 §7health every second to\n" +
                        "§7allies. Lasts §610 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        DamageHealCircle damageHealCircle = new DamageHealCircle(player, player.getTargetBlock((HashSet<Byte>) null, 15).getLocation(), 5, 10, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawn();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "RAIN", 10));
        warlordsPlayer.subtractEnergy(energyCost);
        Warlords.damageHealCircles.add(damageHealCircle);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.healingrain.impact", 1, 1);
        }
    }
}

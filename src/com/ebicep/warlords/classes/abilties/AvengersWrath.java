package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class AvengersWrath extends AbstractAbility {

    public AvengersWrath() {
        super("Avenger's Wrath", 0, 0, 53, 0, 0, 0,
                "§7Burst with incredible holy power,\n" +
                        "§7causing your Avenger's Strikes to\n" +
                        "§7hit up to §e2 §7additional enemies\n" +
                        "§7that are within §e5 §7blocks of your\n" +
                        "§7target. Your energy per second is\n" +
                        "§7increased by §e20 §7for the duration\n" +
                        "§7of the effect. Lasts §612 seconds.");
    }

    @Override
    public void onActivate(Player player) {
        Warlords.getPlayer(player).setWrath(12);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.avengerswrath.activation", 2, 1);
        }
    }
}

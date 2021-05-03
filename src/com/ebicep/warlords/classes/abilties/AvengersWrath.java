package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class AvengersWrath extends AbstractAbility {

    public AvengersWrath() {
        super("Avenger's Wrath", 0, 0, 53, 0, 0, 0, "avengers wrath description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Warlords.getPlayer(e.getPlayer()).setWrath(12);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "paladin.avengerswrath.activation", 1, 1);
        }
    }
}

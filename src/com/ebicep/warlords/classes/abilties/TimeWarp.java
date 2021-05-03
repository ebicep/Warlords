package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class TimeWarp extends AbstractAbility {

    public TimeWarp() {
        super("Time Warp", 0, 0, 29, 30, 0, 0,
                "§7Activate to place a time rune on\n" +
                "§7the ground. After §65 §7seconds,\n" +
                "§7you will warp back to that location\n" +
                "§7and restore §a30% §7of your health");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        Warlords.getTimeWarpPlayers().add(new TimeWarpPlayer(warlordsPlayer, player.getLocation(), player.getLocation().getDirection(), 5));
        warlordsPlayer.subtractEnergy(energyCost);

        // TODO: fix sound for warping back
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "mage.timewarp.activation", 1, 1);
        }
    }
}

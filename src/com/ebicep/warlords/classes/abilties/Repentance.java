package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Repentance extends AbstractAbility {

    public Repentance() {
        super("Repentance", 0, 0, 32, 20, 0, 0, "repentance description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setRepentance(12);
        warlordsPlayer.setRepentanceCounter(warlordsPlayer.getRepentanceCounter() + 2000);


        // TODO: find spiritguards rep sound
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "warrior.seismicwave.activation", 1, 1);
        }
    }
}

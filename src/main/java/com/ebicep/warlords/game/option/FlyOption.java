package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FlyOption implements Option {

    private boolean flyEnabled = false;

    public void setFlyEnabled(boolean flyEnabled) {
        this.flyEnabled = flyEnabled;
    }

    @Override
    public void onPlayerReJoinGame(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setAllowFlight(flyEnabled);
                player.setFlying(flyEnabled);
            }
        }.runTaskLater(Warlords.getInstance(), 2); // delay bc ?
    }

}

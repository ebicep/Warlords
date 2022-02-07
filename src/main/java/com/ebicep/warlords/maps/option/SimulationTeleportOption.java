package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.events.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.maps.flags.SpawnFlagLocation;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.event.EventHandler;

public class SimulationTeleportOption extends TeleportOnEventOption {

    @EventHandler
    public void onFlagReturn(WarlordsFlagUpdatedEvent e) {
        if (e.getNew() instanceof SpawnFlagLocation) {
            preventPlayerMovement = true;

            for (WarlordsPlayer wp : PlayerFilter
                    .playingGame(game)
                    .isAlive()
            ) {
                wp.respawn();
            }

            new GameRunnable(game) {
                @Override
                public void run() {
                    preventPlayerMovement = false;
                }
            }.runTaskLater(40);
        }
    }
}

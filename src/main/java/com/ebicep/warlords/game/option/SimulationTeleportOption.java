package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.game.flags.SpawnFlagLocation;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;

public class SimulationTeleportOption extends TeleportOnEventOption {

    @EventHandler
    public void onFlagReturn(WarlordsFlagUpdatedEvent e) {
        if (e.getNew() instanceof SpawnFlagLocation) {
            preventPlayerMovement = true;

            for (WarlordsEntity wp : PlayerFilter
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

package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.events.WarlordsRespawnEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.event.EventHandler;

public class DuelsTeleportOption extends TeleportOnEventOption {

    @EventHandler
    public void onDeathEvent(WarlordsDeathEvent e) {
        for (WarlordsPlayer wp : PlayerFilter
                .playingGame(game)
                .isAlive()
        ) {
            wp.respawn();
        }
        preventPlayerMovement = true;
    }

    @EventHandler
    public void onRespawnEvent(WarlordsRespawnEvent e) {
        preventPlayerMovement = false;
    }
}

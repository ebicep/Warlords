package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NoRespawnIfOfflineOption implements Option, Listener {

    @Override
    public void register(Game game) {
        game.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEvent(WarlordsRespawnEvent event) {
        if (!event.getPlayer().isOnline()) {
            event.setCancelled(true);
        }
    }

}

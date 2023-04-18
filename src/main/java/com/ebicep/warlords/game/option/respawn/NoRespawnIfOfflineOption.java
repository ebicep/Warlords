package com.ebicep.warlords.game.option.respawn;

import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class NoRespawnIfOfflineOption implements Option, Listener {

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEvent(WarlordsRespawnEvent event) {
        if (!event.getWarlordsEntity().isOnline()) {
            event.setCancelled(true);
        }
    }

}

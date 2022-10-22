package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;

public class DieOnLogoutOption implements Option {

    @Override
    public void start(@Nonnull Game game) {
        game.registerEvents(new Listener() {

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                game.warlordsPlayers()
                        .filter(warlordsPlayer -> warlordsPlayer.getUuid().equals(event.getPlayer().getUniqueId()))
                        .findAny()
                        .ifPresent(warlordsPlayer -> {
                            if (warlordsPlayer.isAlive()) {
                                warlordsPlayer.die(null);
                            }
                        });
            }

        });
    }
}

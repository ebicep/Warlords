package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class TeleportOnEventOption implements Option, Listener {

    boolean preventPlayerMovement = false;
    Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        game.registerEvents(new Listener() {
            @EventHandler
            public void cancelMovement(PlayerMoveEvent e) {
                WarlordsPlayer wp = Warlords.getPlayer(e.getPlayer());
                if (
                    wp != null &&
                    wp.getGame() == game &&
                    preventPlayerMovement &&
                    (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) &&
                    !(e instanceof PlayerTeleportEvent) &&
                    e.getPlayer().getGameMode() != GameMode.SPECTATOR
                ) {
                    e.getPlayer().teleport(e.getFrom());
                    e.getPlayer().setVelocity(new Vector(0, 0, 0));
                }
            }
        });
        game.registerEvents(this);
    }
}

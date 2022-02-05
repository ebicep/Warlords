package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.events.WarlordsRespawnEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class DuelsRespawnOption implements Option, Listener {

    boolean preventPlayerMovement = false;
    Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        game.registerEvents(this);
    }

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

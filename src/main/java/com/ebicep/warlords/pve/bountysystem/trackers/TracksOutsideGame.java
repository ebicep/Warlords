package com.ebicep.warlords.pve.bountysystem.trackers;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks events that happen outside of the game.
 * <p>
 * List of events that can be overriden by implementing this, there is a single static listener class that calls these methods, for all player bounties
 */
public interface TracksOutsideGame {

    static Listener getListener() {
        return new Listener() {

            //TODO

            public List<DatabasePlayer> getOnlineDatabasePlayers() {
                List<DatabasePlayer> databasePlayers = new ArrayList<>();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    DatabaseManager.getPlayer(onlinePlayer, databasePlayers::add);
                }
                return databasePlayers;
            }

        };
    }

    default void onWeaponSalvage(AbstractWeapon weapon) {
    }

}

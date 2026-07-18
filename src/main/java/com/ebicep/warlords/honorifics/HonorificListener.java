package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.AddCurrencyEvent;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.events.player.SupplyDropCallEvent;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.events.StarPieceSynthesizedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HonorificListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        HonorificManager.preload(event.getUniqueId());
    }

    @EventHandler
    public void onFirstLoad(DatabasePlayerFirstLoadEvent event) {
        HonorificManager.forceChallengeRefresh(event.getDatabasePlayer(), event.getPlayer());
    }

    @EventHandler
    public void onStarPieceSynthesized(StarPieceSynthesizedEvent event) {
        HonorificManager.recordStarPieceSynthesis(event.getUUID(), 1);
    }

    @EventHandler
    public void onSupplyDropCall(SupplyDropCallEvent event) {
        HonorificManager.recordSupplyDrops(event.getUUID(), event.getAmount());
    }

    @EventHandler
    public void onCurrencyChanged(AddCurrencyEvent event) {
        if (event.getAmount() >= 0 || !Currencies.STAR_PIECES.contains(event.getCurrency())) {
            return;
        }
        DatabasePlayer databasePlayer = HonorificManager.findDatabasePlayer(event.getDatabasePlayerPvE());
        if (databasePlayer == null || !DatabaseManager.inCache(databasePlayer.getUuid(), PlayersCollections.LIFETIME)) {
            return;
        }
        HonorificManager.recordStarPiecesUsed(databasePlayer.getUuid(), -event.getAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        HonorificManager.unload(event.getPlayer().getUniqueId());
    }
}

package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.AddCurrencyEvent;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.events.player.SupplyDropCallEvent;
import com.ebicep.warlords.featureflags.FeatureFlags;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.events.StarPieceSynthesizedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HonorificListener implements Listener {

    private static boolean honorificsEnabled() {
        return FeatureFlags.isFeatureEnabled(FeatureFlags.HONORIFICS, null);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!honorificsEnabled()) {
            return;
        }
        HonorificManager.preload(event.getUniqueId());
    }

    @EventHandler
    public void onFirstLoad(DatabasePlayerFirstLoadEvent event) {
        if (!honorificsEnabled()) {
            return;
        }
        HonorificManager.forceChallengeRefresh(event.getDatabasePlayer(), event.getPlayer());
    }

    @EventHandler
    public void onStarPieceSynthesized(StarPieceSynthesizedEvent event) {
        if (!honorificsEnabled()) {
            return;
        }
        HonorificManager.recordStarPieceSynthesis(event.getUUID(), 1);
    }

    @EventHandler
    public void onSupplyDropCall(SupplyDropCallEvent event) {
        if (!honorificsEnabled()) {
            return;
        }
        HonorificManager.recordSupplyDrops(event.getUUID(), event.getAmount());
    }

    @EventHandler
    public void onCurrencyChanged(AddCurrencyEvent event) {
        if (!honorificsEnabled()) {
            return;
        }
        if (event.getAmount() >= 0 || !Currencies.STAR_PIECES.contains(event.getCurrency())) {
            return;
        }
        DatabasePlayer databasePlayer = HonorificManager.findDatabasePlayer(event.getDatabasePlayerPvE());
        if (databasePlayer == null || !DatabaseManager.inCache(databasePlayer.getUuid(), PlayersCollections.LIFETIME)) {
            return;
        }
        HonorificManager.recordStarPiecesUsed(databasePlayer.getUuid(), -event.getAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameWin(WarlordsGameTriggerWinEvent event) {
        if (!honorificsEnabled()) {
            return;
        }
        if (event.getGame().getAddons().contains(GameAddon.CUSTOM_GAME)) {
            return;
        }
        event.getGame().warlordsPlayers().forEach(warlordsPlayer ->
                HonorificManager.recordSingleGameDamage(
                        warlordsPlayer.getUuid(),
                        warlordsPlayer.getMinuteStats().total().getDamage()
                )
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        if (!honorificsEnabled()) {
            return;
        }
        HonorificManager.unload(event.getPlayer().getUniqueId());
    }
}

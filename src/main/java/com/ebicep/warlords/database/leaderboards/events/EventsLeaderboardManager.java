package com.ebicep.warlords.database.leaderboards.events;

import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;

public class EventsLeaderboardManager {

    public static final HashMap<EventLeaderboard, String> EVENT_LEADERBOARDS = new HashMap<>();

    public static void create() {
        if (!Warlords.hologramsEnabled || !StatsLeaderboardManager.enabled) {
            return;
        }
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null) {
            return;
        }
        currentGameEvent.getEvent().addLeaderboards(currentGameEvent, EVENT_LEADERBOARDS);
        EVENT_LEADERBOARDS.forEach((eventLeaderboard, s) -> eventLeaderboard.resetHolograms(null, "", s));
        Bukkit.getOnlinePlayers().forEach(EventsLeaderboardManager::resetVisibility);
    }

    public static void clearHolograms() {
        EVENT_LEADERBOARDS.forEach((eventLeaderboard, s) -> {
            eventLeaderboard.getSortedHolograms()
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(Hologram::deleteHologram);
            eventLeaderboard.getSortedHolograms().clear();
        });
    }

    public static void resetVisibility(Player player) {
        if (!Warlords.hologramsEnabled || !StatsLeaderboardManager.enabled) {
            return;
        }
        StatsLeaderboardManager.validatePlayerHolograms(player);
        EVENT_LEADERBOARDS.forEach((eventLeaderboard, s) -> {
            eventLeaderboard.getSortedHolograms()
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(hologram -> HologramManager.updateHologram(player, hologram));
        });
    }

}

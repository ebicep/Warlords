package com.ebicep.warlords.database.leaderboards.stats.sections;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import me.filoghost.holographicdisplays.api.hologram.Hologram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>ALL
 * <p>Comps
 * <p>Pubs
 */
public class StatsLeaderboardCategory<
        DatabaseGameT extends DatabaseGameBase<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerBase,
        T extends Stats<DatabaseGameT, DatabaseGamePlayerT>> {

    private final Function<DatabasePlayer, T> statFunction;
    private final String categoryName;
    private final String shortName;
    private final List<StatsLeaderboard> statsLeaderboards = new ArrayList<>();

    public StatsLeaderboardCategory(Function<DatabasePlayer, T> statFunction, String categoryName, String shortName) {
        this.statFunction = statFunction;
        this.categoryName = categoryName;
        this.shortName = shortName;
    }

    public void resetLeaderboards(PlayersCollections collection, Predicate<DatabasePlayer> externalFilter, String subTitle) {
        for (StatsLeaderboard statsLeaderboard : getStatsLeaderboards()) {
            statsLeaderboard.resetHolograms(collection, externalFilter, getShortName(), subTitle);
        }
    }

    public List<List<Hologram>> getCollectionHologramPaged(PlayersCollections collections) {
        return getStatsLeaderboards().stream()
                                     .flatMap(statsLeaderboard -> statsLeaderboard.getSortedHolograms(collections).stream())
                                     .toList();
    }

    public List<List<Hologram>> getAllHologramsPaged() {
        return getStatsLeaderboards().stream()
                                     .flatMap(statsLeaderboard -> statsLeaderboard.getSortedTimedHolograms().values().stream())
                                     .flatMap(Collection::stream)
                                     .toList();
    }

    public List<Hologram> getAllHolograms() {
        return getStatsLeaderboards().stream()
                                     .flatMap(statsLeaderboard -> statsLeaderboard.getSortedTimedHolograms().values().stream())
                                     .flatMap(Collection::stream)
                                     .flatMap(Collection::stream)
                                     .toList();
    }

    public List<StatsLeaderboard> getLeaderboards() {
        return getStatsLeaderboards();
    }

    public Function<DatabasePlayer, T> getStatFunction() {
        return statFunction;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getShortName() {
        return shortName;
    }

    public List<StatsLeaderboard> getStatsLeaderboards() {
        return statsLeaderboards;
    }
}

package com.ebicep.warlords.database.leaderboards.stats.sections;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import me.filoghost.holographicdisplays.api.hologram.Hologram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>ALL
 * <p>Comps
 * <p>Pubs
 */
public class StatsLeaderboardCategory<T extends AbstractDatabaseStatInformation> {

    public final Function<DatabasePlayer, T> statFunction;
    public final String categoryName;

    public final List<StatsLeaderboard> statsLeaderboards = new ArrayList<>();

    public final List<List<Hologram>> lifeTimeHolograms = new ArrayList<>();
    public final List<List<Hologram>> season6Holograms = new ArrayList<>();
    public final List<List<Hologram>> season5Holograms = new ArrayList<>();
    public final List<List<Hologram>> season4Holograms = new ArrayList<>();
    public final List<List<Hologram>> weeklyHolograms = new ArrayList<>();
    public final List<List<Hologram>> dailyHolograms = new ArrayList<>();

    public StatsLeaderboardCategory(Function<DatabasePlayer, T> statFunction, String categoryName) {
        this.statFunction = statFunction;
        this.categoryName = categoryName;
    }

    public void resetLeaderboards(PlayersCollections collection, Set<DatabasePlayer> databasePlayers, String subTitle) {
        for (StatsLeaderboard statsLeaderboard : statsLeaderboards) {
            //resetting sort then adding new sorted values
            statsLeaderboard.resetSortedPlayers(databasePlayers, collection);
            //creating leaderboard
            List<Hologram> holograms = new ArrayList<>();
            for (int i = 0; i < StatsLeaderboard.MAX_PAGES; i++) {
                holograms.add(statsLeaderboard.createHologram(collection, i, subTitle + " - " + (categoryName.isEmpty() ? "" : categoryName + " - ") + collection.name));
            }
            getCollectionHologramPaged(collection).add(holograms);
        }
    }

    public List<List<Hologram>> getCollectionHologramPaged(PlayersCollections collections) {
        if (collections == PlayersCollections.LIFETIME) return this.lifeTimeHolograms;
        if (collections == PlayersCollections.SEASON_6) return this.season6Holograms;
        if (collections == PlayersCollections.SEASON_5) return this.season5Holograms;
        if (collections == PlayersCollections.SEASON_4) return this.season4Holograms;
        if (collections == PlayersCollections.WEEKLY) return this.weeklyHolograms;
        if (collections == PlayersCollections.DAILY) return this.dailyHolograms;
        return null;
    }

    public void deleteHolograms(PlayersCollections collection) {
        List<List<Hologram>> hologramPaged = getCollectionHologramPaged(collection);
        hologramPaged.forEach(holograms -> holograms.forEach(Hologram::delete));
        hologramPaged.clear();
    }

    public List<List<Hologram>> getAllHologramsPaged() {
        return Stream.of(lifeTimeHolograms,
                        season6Holograms,
                        season5Holograms,
                        season4Holograms,
                        weeklyHolograms,
                        dailyHolograms)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<Hologram> getAllHolograms() {
        return Stream.of(lifeTimeHolograms,
                        season6Holograms,
                        season5Holograms,
                        season4Holograms,
                        weeklyHolograms,
                        dailyHolograms)
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public List<StatsLeaderboard> getLeaderboards() {
        return statsLeaderboards;
    }

}

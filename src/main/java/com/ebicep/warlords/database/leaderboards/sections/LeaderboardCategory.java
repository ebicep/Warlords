package com.ebicep.warlords.database.leaderboards.sections;

import com.ebicep.warlords.database.leaderboards.Leaderboard;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LeaderboardCategory<T extends AbstractDatabaseStatInformation> {

    public final Function<DatabasePlayer, T> statFunction;

    public final List<Leaderboard> leaderboards = new ArrayList<>();

    public final List<Hologram> lifeTimeHolograms = new ArrayList<>();
    public final List<Hologram> season6Holograms = new ArrayList<>();
    public final List<Hologram> season5Holograms = new ArrayList<>();
    public final List<Hologram> season4Holograms = new ArrayList<>();
    public final List<Hologram> weeklyHolograms = new ArrayList<>();
    public final List<Hologram> dailyHolograms = new ArrayList<>();

    public LeaderboardCategory(Function<DatabasePlayer, T> statFunction) {
        this.statFunction = statFunction;
    }

    public List<Hologram> getCollectionHologram(PlayersCollections collections) {
        if (collections == PlayersCollections.LIFETIME) return this.lifeTimeHolograms;
        if (collections == PlayersCollections.SEASON_6) return this.season6Holograms;
        if (collections == PlayersCollections.SEASON_5) return this.season5Holograms;
        if (collections == PlayersCollections.SEASON_4) return this.season4Holograms;
        if (collections == PlayersCollections.WEEKLY) return this.weeklyHolograms;
        if (collections == PlayersCollections.DAILY) return this.dailyHolograms;
        return null;
    }

    public List<Hologram> getAllHolograms() {
        return Stream.of(lifeTimeHolograms,
                        season6Holograms,
                        season5Holograms,
                        season4Holograms,
                        weeklyHolograms,
                        dailyHolograms)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Function<DatabasePlayer, T> getStatFunction() {
        return statFunction;
    }

    public List<Leaderboard> getLeaderboards() {
        return leaderboards;
    }

    public List<Hologram> getLifeTimeHolograms() {
        return lifeTimeHolograms;
    }

    public List<Hologram> getSeason6Holograms() {
        return season6Holograms;
    }

    public List<Hologram> getSeason5Holograms() {
        return season5Holograms;
    }

    public List<Hologram> getSeason4Holograms() {
        return season4Holograms;
    }

    public List<Hologram> getWeeklyHolograms() {
        return weeklyHolograms;
    }

    public List<Hologram> getDailyHolograms() {
        return dailyHolograms;
    }
}

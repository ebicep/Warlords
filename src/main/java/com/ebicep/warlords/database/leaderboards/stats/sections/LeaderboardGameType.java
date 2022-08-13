package com.ebicep.warlords.database.leaderboards.stats.sections;

import com.ebicep.warlords.database.leaderboards.stats.Leaderboard;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import me.filoghost.holographicdisplays.api.hologram.Hologram;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;

/**
 * Different gamemodes
 * <p>ALL
 * <p>CTF
 */
public abstract class LeaderboardGameType<T extends AbstractDatabaseStatInformation> {

    protected final LeaderboardCategory<T> general;
    protected final LeaderboardCategory<T> comps;
    protected final LeaderboardCategory<T> pubs;

    public LeaderboardGameType(LeaderboardCategory<T> general, LeaderboardCategory<T> comps, LeaderboardCategory<T> pubs) {
        this.general = general;
        this.comps = comps;
        this.pubs = pubs;
    }

    public abstract String getSubTitle();

    public abstract void addExtraLeaderboards(LeaderboardCategory<T> leaderboardCategory);

    public void resetLeaderboards(PlayersCollections collection, Set<DatabasePlayer> databasePlayers) {
        String subTitle = getSubTitle();
        general.resetLeaderboards(collection, databasePlayers, subTitle);
        comps.resetLeaderboards(collection, databasePlayers, subTitle);
        pubs.resetLeaderboards(collection, databasePlayers, subTitle);
    }

    public void addBaseLeaderboards(LeaderboardCategory<T> leaderboardCategory) {
        leaderboardCategory.getAllHolograms().forEach(Hologram::delete);

        List<Leaderboard> leaderboards = leaderboardCategory.getLeaderboards();
        leaderboards.clear();

        leaderboards.add(new Leaderboard("Wins", LEAD_2, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getWins(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getWins())));
        leaderboards.add(new Leaderboard("Losses", CIRCULAR_1_CENTER, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getLosses(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getLosses())));
        leaderboards.add(new Leaderboard("Plays", LEAD_1, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getPlays(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getPlays())));
        leaderboards.add(new Leaderboard("Kills", LEAD_3, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getKills(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getKills())));
        leaderboards.add(new Leaderboard("Assists", CIRCULAR_1_OUTER_3, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getAssists(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getAssists())));
        leaderboards.add(new Leaderboard("Deaths", CIRCULAR_1_OUTER_4, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDeaths(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getDeaths())));
        leaderboards.add(new Leaderboard("Damage", CIRCULAR_1_OUTER_6, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDamage(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getDamage())));
        leaderboards.add(new Leaderboard("Healing", CIRCULAR_1_OUTER_5, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getHealing(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getHealing())));
        leaderboards.add(new Leaderboard("Absorbed", CIRCULAR_1_OUTER_1, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getAbsorbed(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getAbsorbed())));


        leaderboards.add(new Leaderboard("DHP", CIRCULAR_2_OUTER_3, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDHP(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getDHP())));
        leaderboards.add(new Leaderboard("DHP Per Game", LEAD_4, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDHPPerGame(), databasePlayer -> NumberFormat.addCommaAndRound(Math.round((double) (leaderboardCategory.statFunction.apply(databasePlayer).getDHPPerGame()) * 10) / 10d)));
        leaderboards.add(new Leaderboard("Kills Per Game", CIRCULAR_2_OUTER_2, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getKillsPerGame(), databasePlayer -> String.valueOf(Math.round(leaderboardCategory.statFunction.apply(databasePlayer).getKillsPerGame() * 10) / 10d)));
        leaderboards.add(new Leaderboard("Deaths Per Game", CIRCULAR_2_OUTER_1, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDeathsPerGame(), databasePlayer -> String.valueOf(Math.round(leaderboardCategory.statFunction.apply(databasePlayer).getDeathsPerGame() * 10) / 10d)));
        leaderboards.add(new Leaderboard("Kills/Assists Per Game", CIRCULAR_2_OUTER_4, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getKillsAssistsPerGame(), databasePlayer -> String.valueOf(Math.round(leaderboardCategory.statFunction.apply(databasePlayer).getKillsAssistsPerGame() * 10) / 10d)));

        leaderboards.add(new Leaderboard("Experience", CENTER_BOARD, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getExperience())));


        this.addExtraLeaderboards(leaderboardCategory);
    }


    public List<LeaderboardCategory<T>> getCategories() {
        return Arrays.asList(general, comps, pubs);
    }

    public LeaderboardCategory<T> getGeneral() {
        return general;
    }

    public LeaderboardCategory<T> getComps() {
        return comps;
    }

    public LeaderboardCategory<T> getPubs() {
        return pubs;
    }
}

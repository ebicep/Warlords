package com.ebicep.warlords.database.leaderboards.sections;

import com.ebicep.warlords.database.leaderboards.Leaderboard;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.util.java.NumberFormat;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public abstract class LeaderboardGameType<T extends AbstractDatabaseStatInformation> {

    public static World world = Bukkit.getWorld("MainLobby");
    protected final LeaderboardCategory<T> general;
    protected final LeaderboardCategory<T> comps;
    protected final LeaderboardCategory<T> pubs;

    public LeaderboardGameType(LeaderboardCategory<T> general, LeaderboardCategory<T> comps, LeaderboardCategory<T> pubs) {
        this.general = general;
        this.comps = comps;
        this.pubs = pubs;
    }

    public void addBaseLeaderboards(LeaderboardCategory<T> leaderboardCategory) {
        leaderboardCategory.getAllHolograms().forEach(Hologram::delete);

        List<Leaderboard> leaderboards = leaderboardCategory.getLeaderboards();
        leaderboards.clear();

        leaderboards.add(new Leaderboard("Wins",
                new Location(world, -2558.5, 56, 712.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getWins(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getWins())));
        leaderboards.add(new Leaderboard("Losses", new Location(world, -2608.5, 52, 728.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getLosses(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getLosses())));
        leaderboards.add(new Leaderboard("Plays", new Location(world, -2564.5, 56, 712.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getPlays(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getPlays())));
        leaderboards.add(new Leaderboard("Kills", new Location(world, -2552.5, 56, 712.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getKills(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getKills())));
        leaderboards.add(new Leaderboard("Assists", new Location(world, -2616.5, 52, 733.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getAssists(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getAssists())));
        leaderboards.add(new Leaderboard("Deaths", new Location(world, -2616.5, 52, 723.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDeaths(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getDeaths())));
        leaderboards.add(new Leaderboard("Damage", new Location(world, -2600.5, 52, 723.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDamage(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getDamage())));
        leaderboards.add(new Leaderboard("Healing", new Location(world, -2608.5, 52, 719.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getHealing(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getHealing())));
        leaderboards.add(new Leaderboard("Absorbed", new Location(world, -2600.5, 52, 733.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getAbsorbed(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getAbsorbed())));


        leaderboards.add(new Leaderboard("DHP", new Location(world, -2619.5, 66.5, 721.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDHP(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getDHP())));
        leaderboards.add(new Leaderboard("DHP Per Game", new Location(world, -2546.5, 56, 712.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDHPPerGame(),
                databasePlayer -> NumberFormat.addCommaAndRound(Math.round((double) (leaderboardCategory.statFunction.apply(databasePlayer).getDHPPerGame()) * 10) / 10d)));
        leaderboards.add(new Leaderboard("Kills Per Game", new Location(world, -2619.5, 66.5, 735.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getKillsPerGame(),
                databasePlayer -> String.valueOf(Math.round(leaderboardCategory.statFunction.apply(databasePlayer).getKillsPerGame() * 10) / 10d)));
        leaderboards.add(new Leaderboard("Deaths Per Game", new Location(world, -2608.5, 67, 738.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getDeathsPerGame(),
                databasePlayer -> String.valueOf(Math.round(leaderboardCategory.statFunction.apply(databasePlayer).getDeathsPerGame() * 10) / 10d)));
        leaderboards.add(new Leaderboard("Kills/Assists Per Game", new Location(world, -2608.5, 67, 719.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getKillsAssistsPerGame(),
                databasePlayer -> String.valueOf(Math.round(leaderboardCategory.statFunction.apply(databasePlayer).getKillsAssistsPerGame() * 10) / 10d)));

        leaderboards.add(new Leaderboard("Experience", new Location(world, -2526.5, 57, 744.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getExperience())));


        this.addExtraLeaderboards(leaderboardCategory);
    }

    public abstract void addExtraLeaderboards(LeaderboardCategory<T> leaderboardCategory);

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

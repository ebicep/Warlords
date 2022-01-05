package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;

import java.util.List;

public enum PlayersCollections {

    ALL_TIME("LifeTime", "Players_Information", "playersAllTime", LeaderboardManager.lifeTimeHolograms),
    SEASON_5("Season 5", "Players_Information_Season_5", "playersSeason5", LeaderboardManager.season5Holograms),
    SEASON_4("Season 4", "Players_Information_Season_4", "playersSeason4", LeaderboardManager.season4Holograms),
    WEEKLY("Weekly", "Players_Information_Weekly", "playersWeekly", LeaderboardManager.weeklyHolograms),
    DAILY("Daily", "Players_Information_Daily", "playersDaily", LeaderboardManager.dailyHolograms),
    ;

    public String name;
    public String collectionName;
    public String cacheName;
    public List<Hologram> leaderboardHolograms;

    PlayersCollections(String name, String collectionName, String cacheName, List<Hologram> leaderboardHolograms) {
        this.name = name;
        this.collectionName = collectionName;
        this.cacheName = cacheName;
        this.leaderboardHolograms = leaderboardHolograms;
    }
}

package com.ebicep.warlords.database.leaderboards;

import com.ebicep.warlords.database.leaderboards.stats.LeaderboardManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import me.filoghost.holographicdisplays.api.hologram.Hologram;

import java.util.ArrayList;
import java.util.List;

public class PlayerLeaderboardInfo {

    private int gameHologram = 0;
    private LeaderboardManager.GameType statsGameType = LeaderboardManager.GameType.ALL;
    private LeaderboardManager.Category statsCategory = LeaderboardManager.Category.ALL;
    private PlayersCollections statsTime = PlayersCollections.LIFETIME;
    private List<Hologram> holograms = new ArrayList<>();

    public int getGameHologram() {
        return gameHologram;
    }

    public void setGameHologram(int gameHologram) {
        this.gameHologram = gameHologram;
    }

    public void resetGameHologram() {
        this.gameHologram = DatabaseGameBase.previousGames.size() - 1;
    }

    public LeaderboardManager.GameType getStatsGameType() {
        return statsGameType;
    }

    public void setStatsGameType(LeaderboardManager.GameType statsGameType) {
        this.statsGameType = statsGameType;
    }

    public LeaderboardManager.Category getStatsCategory() {
        return statsCategory;
    }

    public void setStatsCategory(LeaderboardManager.Category statsCategory) {
        this.statsCategory = statsCategory;
    }

    public PlayersCollections getStatsTime() {
        return statsTime;
    }

    public void setStatsTime(PlayersCollections statsTime) {
        this.statsTime = statsTime;
    }

    public List<Hologram> getHolograms() {
        return holograms;
    }

    public void setHolograms(List<Hologram> holograms) {
        this.holograms = holograms;
    }
}

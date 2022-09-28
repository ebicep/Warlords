package com.ebicep.warlords.database.leaderboards;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import me.filoghost.holographicdisplays.api.hologram.Hologram;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PlayerLeaderboardInfo {

    private int gameHologram = 0;
    @Nonnull
    private StatsLeaderboardManager.GameType statsGameType = StatsLeaderboardManager.GameType.PVE;
    @Nonnull
    private StatsLeaderboardManager.Category statsCategory = StatsLeaderboardManager.Category.ALL;
    @Nonnull
    private PlayersCollections statsTime = PlayersCollections.LIFETIME;
    private List<Hologram> holograms = new ArrayList<>();
    private int page = 0;

    public int getGameHologram() {
        return gameHologram;
    }

    public void setGameHologram(int gameHologram) {
        this.gameHologram = gameHologram;
    }

    public void resetGameHologram() {
        this.gameHologram = DatabaseGameBase.previousGames.size() - 1;
    }

    @Nonnull
    public StatsLeaderboardManager.GameType getStatsGameType() {
        return statsGameType;
    }

    public void setStatsGameType(@Nonnull StatsLeaderboardManager.GameType statsGameType) {
        this.statsGameType = statsGameType;
    }

    @Nonnull
    public StatsLeaderboardManager.Category getStatsCategory() {
        return statsCategory;
    }

    public void setStatsCategory(@Nonnull StatsLeaderboardManager.Category statsCategory) {
        this.statsCategory = statsCategory;
    }

    @Nonnull
    public PlayersCollections getStatsTime() {
        return statsTime;
    }

    public void setStatsTime(@Nonnull PlayersCollections statsTime) {
        this.statsTime = statsTime;
    }

    public void clearHolograms() {
        this.holograms.forEach(Hologram::delete);
        this.holograms.clear();
    }

    public void setHolograms(List<Hologram> holograms) {
        this.holograms = holograms;
    }

    public int getPage() {
        return page;
    }

    public int getPageBefore() {
        return page == 0 ? StatsLeaderboard.MAX_PAGES - 1 : page - 1;
    }

    public int getPageAfter() {
        return page + 1 == StatsLeaderboard.MAX_PAGES ? 0 : page + 1;
    }

    public String getPageRange(int page) {
        return page * StatsLeaderboard.PLAYERS_PER_PAGE + 1 + " - " + (page + 1) * StatsLeaderboard.PLAYERS_PER_PAGE;
    }

    public void setPage(int page) {
        this.page = page;
    }

}

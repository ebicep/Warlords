package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStat;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class DatabasePlayerWaveDefenseStats implements MultiStat<DatabaseGamePvEWaveDefense, DatabaseGamePlayerPvEWaveDefense> {

    @Field("easy_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats easyStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("normal_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats normalStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("hard_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats hardStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("extreme_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats extremeStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("endless_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats endlessStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();

    public DatabasePlayerWaveDefenseStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEWaveDefense databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEWaveDefense gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        DatabasePlayerPvEWaveDefenseDifficultyStats difficultyStats = getDifficultyStats(databaseGame.getDifficulty());
        if (difficultyStats != null) {
            difficultyStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Error: Difficulty stats is null", NamedTextColor.GREEN));
        }
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getDifficultyStats(DifficultyIndex difficultyIndex) {
        return switch (difficultyIndex) {
            case EASY -> getEasyStats();
            case NORMAL -> getNormalStats();
            case HARD -> getHardStats();
            case EXTREME -> getExtremeStats();
            case ENDLESS -> getEndlessStats();
            default -> null;
        };
    }


    public DatabasePlayerPvEWaveDefenseDifficultyStats getEasyStats() {
        return easyStats;
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getNormalStats() {
        return normalStats;
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getHardStats() {
        return hardStats;
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getExtremeStats() {
        return extremeStats;
    }

    public DatabasePlayerPvEWaveDefenseDifficultyStats getEndlessStats() {
        return endlessStats;
    }

    public void setEndlessStats(DatabasePlayerPvEWaveDefenseDifficultyStats endlessStats) {
        this.endlessStats = endlessStats;
    }

    public void setHardStats(DatabasePlayerPvEWaveDefenseDifficultyStats hardStats) {
        this.hardStats = hardStats;
    }

    public void setNormalStats(DatabasePlayerPvEWaveDefenseDifficultyStats normalStats) {
        this.normalStats = normalStats;
    }

    public void setEasyStats(DatabasePlayerPvEWaveDefenseDifficultyStats easyStats) {
        this.easyStats = easyStats;
    }

    @Override
    public <T extends StatsWarlordsClasses<?, ?, ?, ?>> List<T> getStats() {
        return List.of(
                (T) easyStats,
                (T) normalStats,
                (T) hardStats,
                (T) extremeStats,
                (T) endlessStats
        );
    }
}

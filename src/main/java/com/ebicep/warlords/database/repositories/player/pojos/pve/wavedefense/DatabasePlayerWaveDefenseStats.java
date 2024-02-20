package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabasePlayerWaveDefenseStats extends DatabasePlayerPvEWaveDefenseDifficultyStats {

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
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        PvEDatabaseStatInformation difficultyStats = getDifficultyStats(((DatabaseGamePvEWaveDefense) databaseGame).getDifficulty());
        if (difficultyStats != null) {
            difficultyStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Error: Difficulty stats is null", NamedTextColor.GREEN));
        }
    }

    public PvEDatabaseStatInformation getDifficultyStats(DifficultyIndex difficultyIndex) {
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

}

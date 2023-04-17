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
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabasePlayerWaveDefenseStats extends DatabasePlayerPvEWaveDefenseDifficultyStats {

    @Field("easy_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats easyStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("normal_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats normalStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("hard_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats hardStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();
    @Field("endless_stats")
    private DatabasePlayerPvEWaveDefenseDifficultyStats endlessStats = new DatabasePlayerPvEWaveDefenseDifficultyStats();

    public DatabasePlayerWaveDefenseStats() {
    }

    @Override
    public void updateCustomStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        PvEDatabaseStatInformation difficultyStats = getDifficultyStats(((DatabaseGamePvEWaveDefense) databaseGame).getDifficulty());
        if (difficultyStats != null) {
            difficultyStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatChannels.sendDebugMessage((CommandIssuer) null, ChatColor.RED + "Error: Difficulty stats is null", true);
        }
    }

    public PvEDatabaseStatInformation getDifficultyStats(DifficultyIndex difficultyIndex) {
        switch (difficultyIndex) {
            case EASY:
                return getEasyStats();
            case NORMAL:
                return getNormalStats();
            case HARD:
                return getHardStats();
            case ENDLESS:
                return getEndlessStats();
        }
        return null;
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

    public DatabasePlayerPvEWaveDefenseDifficultyStats getEndlessStats() {
        return endlessStats;
    }

    public void setEasyStats(DatabasePlayerPvEWaveDefenseDifficultyStats easyStats) {
        this.easyStats = easyStats;
    }

    public void setNormalStats(DatabasePlayerPvEWaveDefenseDifficultyStats normalStats) {
        this.normalStats = normalStats;
    }

    public void setHardStats(DatabasePlayerPvEWaveDefenseDifficultyStats hardStats) {
        this.hardStats = hardStats;
    }

    public void setEndlessStats(DatabasePlayerPvEWaveDefenseDifficultyStats endlessStats) {
        this.endlessStats = endlessStats;
    }
}

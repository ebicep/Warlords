package com.ebicep.warlords.database.repositories.games.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.pve.DifficultyIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Document(collection = "Games_Information_PvE")
public class DatabaseGamePvE extends DatabaseGameBase {

    private DifficultyIndex difficulty;
    private int wavesCleared;
    private int timeElapsed;
    private int totalMobsKilled;
    private List<DatabaseGamePlayerPvE> players;

    public DatabaseGamePvE() {

    }

    public DatabaseGamePvE(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        //this.difficulty =
        for (Option option : game.getOptions()) {
            if (option instanceof WaveDefenseOption) {
                this.wavesCleared = ((WaveDefenseOption) option).getWaveCounter() - 1;

            }
        }
        this.timeElapsed = RecordTimeElapsedOption.getTimeElapsed(game);
        this.totalMobsKilled = players.stream().mapToInt(DatabaseGamePlayerBase::getTotalKills).sum();
        game.warlordsEntities().forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvE(warlordsPlayer)));

    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, boolean add) {
        players.forEach(databaseGamePlayerPvE -> DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame, databaseGamePlayerPvE, add));
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return DatabaseGamePlayerResult.NONE;
    }

    @Override
    public void createHolograms() {

    }

    @Override
    public String getGameLabel() {
        return null;
    }

    public DifficultyIndex getDifficulty() {
        return difficulty;
    }

    public int getWavesCleared() {
        return wavesCleared;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public int getTotalMobsKilled() {
        return totalMobsKilled;
    }

    public List<DatabaseGamePlayerPvE> getPlayers() {
        return players;
    }
}

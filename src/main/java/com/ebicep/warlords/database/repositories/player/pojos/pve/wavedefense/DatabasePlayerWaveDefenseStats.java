package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvEDifficultyStats;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabasePlayerWaveDefenseStats extends DatabasePlayerPvEDifficultyStats {

    @Field("easy_stats")
    private DatabasePlayerPvEDifficultyStats easyStats = new DatabasePlayerPvEDifficultyStats();
    @Field("normal_stats")
    private DatabasePlayerPvEDifficultyStats normalStats = new DatabasePlayerPvEDifficultyStats();
    @Field("hard_stats")
    private DatabasePlayerPvEDifficultyStats hardStats = new DatabasePlayerPvEDifficultyStats();
    @Field("endless_stats")
    private DatabasePlayerPvEDifficultyStats endlessStats = new DatabasePlayerPvEDifficultyStats();

}

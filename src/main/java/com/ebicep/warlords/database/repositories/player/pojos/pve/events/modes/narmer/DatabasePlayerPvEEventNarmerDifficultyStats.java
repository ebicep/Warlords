package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePlayerPvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePlayerPvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventNarmerDifficultyStats implements MultiPvEEventNarmerStats<
        PvEEventNarmerStatsWarlordsClasses<
                DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>,
                DatabaseGamePlayerPvEEventNarmer,
                PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>,
                PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>,
        DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>,
        DatabaseGamePlayerPvEEventNarmer,
        PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>,
        PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer,
                PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>,
        EventMode {

    @Field("tomb_stats")
    private DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats tombStats = new DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats();
    @Field("event_points_spent")
    private long eventPointsSpent;
    @Field("rewards_purchased")
    private Map<String, Long> rewardsPurchased = new LinkedHashMap<>();
    @Field("completed_bounties")
    private Map<Bounty, Long> completedBounties = new HashMap<>();
    @Field("bounties_completed")
    private int bountiesCompleted = 0;
    @Field("active_bounties")
    private List<AbstractBounty> activeBounties = new ArrayList<>();

    @Override
    public long getEventPointsSpent() {
        return eventPointsSpent;
    }

    @Override
    public void addEventPointsSpent(long eventPointsSpent) {
        this.eventPointsSpent += eventPointsSpent;
    }

    @Override
    public Map<String, Long> getRewardsPurchased() {
        return rewardsPurchased;
    }

    @Override
    public int getEventPlays() {
        return tombStats.getPlays();
    }

    @Override
    public Map<Bounty, Long> getCompletedBounties() {
        return completedBounties;
    }

    @Override
    public int getBountiesCompleted() {
        return bountiesCompleted;
    }

    @Override
    public void addBountiesCompleted() {
        this.bountiesCompleted++;
    }

    @Override
    public List<AbstractBounty> getActiveEventBounties() {
        return activeBounties;
    }

    public DatabasePlayerPvEEventNarmerDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventNarmer databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventNarmer gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (databaseGame instanceof DatabaseGamePvEEventNarmersTomb databaseGamePvEEventNarmersTomb &&
                gamePlayer instanceof DatabaseGamePlayerPvEEventNarmersTomb databaseGamePlayerPvEEventNarmer
        ) {
            this.tombStats.updateStats(databasePlayer,
                    databaseGamePvEEventNarmersTomb,
                    gameMode,
                    databaseGamePlayerPvEEventNarmer,
                    result,
                    multiplier,
                    playersCollection
            );
        }
    }

    public DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats getTombStats() {
        return tombStats;
    }


    @Override
    public Collection<? extends PvEEventNarmerStatsWarlordsClasses<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>, PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>> getStats() {
        return Stream.of(tombStats) // TODO
                     .flatMap(stats -> (Stream<? extends PvEEventNarmerStatsWarlordsClasses<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>, PvEEventNarmerStatsWarlordsSpecs<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer, PvEEventNarmerStats<DatabaseGamePvEEventNarmer<DatabaseGamePlayerPvEEventNarmer>, DatabaseGamePlayerPvEEventNarmer>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               .stream())
                     .toList();
    }

    @Override
    public long getEventPointsCumulative() {
        return MultiPvEEventNarmerStats.super.getEventPointsCumulative();
    }
}

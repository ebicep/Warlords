package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePlayerPvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventMithraDifficultyStats implements MultiPvEEventMithraStats<
        PvEEventMithraStatsWarlordsClasses<
                DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>,
                DatabaseGamePlayerPvEEventMithra,
                PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>,
                PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>>>,
        DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>,
        DatabaseGamePlayerPvEEventMithra,
        PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>,
        PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra,
                PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>>>,
        EventMode {

    @Field("spiders_dwelling_stats")
    private DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats spidersDwellingStats = new DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats();
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
        return spidersDwellingStats.getPlays();
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

    public DatabasePlayerPvEEventMithraDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventMithra databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventMithra gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (databaseGame instanceof DatabaseGamePvEEventSpidersDwelling databaseGamePvEEventSpidersDwelling &&
                gamePlayer instanceof DatabaseGamePlayerPvEEventSpidersDwelling databaseGamePlayerPvEEventMithra
        ) {
            this.spidersDwellingStats.updateStats(databasePlayer,
                    databaseGamePvEEventSpidersDwelling,
                    gameMode,
                    databaseGamePlayerPvEEventMithra,
                    result,
                    multiplier,
                    playersCollection
            );
        }
    }

    public DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats getSpidersDwellingStats() {
        return spidersDwellingStats;
    }

    @Override
    public Collection<? extends PvEEventMithraStatsWarlordsClasses<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>, PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>>>> getStats() {
        return Stream.of(spidersDwellingStats) // TODO
                     .flatMap(stats -> (Stream<? extends PvEEventMithraStatsWarlordsClasses<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>, PvEEventMithraStatsWarlordsSpecs<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra, PvEEventMithraStats<DatabaseGamePvEEventMithra<DatabaseGamePlayerPvEEventMithra>, DatabaseGamePlayerPvEEventMithra>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               .stream())
                     .toList();
    }

    @Override
    public long getEventPointsCumulative() {
        return MultiPvEEventMithraStats.super.getEventPointsCumulative();
    }
}

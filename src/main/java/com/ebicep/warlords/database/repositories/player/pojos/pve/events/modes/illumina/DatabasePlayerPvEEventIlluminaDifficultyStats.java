package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePlayerPvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePlayerPvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventIlluminaDifficultyStats implements MultiPvEEventIlluminaStats<
        PvEEventIlluminaStatsWarlordsClasses<
                DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>,
                DatabaseGamePlayerPvEEventIllumina,
                PvEEventIlluminaStats<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina>,
                PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina>>>,
        DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>,
        DatabaseGamePlayerPvEEventIllumina,
        PvEEventIlluminaStats<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina>,
        PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>,
                DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina>>>,
        EventMode {

    @Field("the_borderline_of_illusion_stats")
    private DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats borderLineOfIllusionStats = new DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats();
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
        return borderLineOfIllusionStats.getPlays();
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

    public DatabasePlayerPvEEventIlluminaDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventIllumina databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventIllumina gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (databaseGame instanceof DatabaseGamePvEEventTheBorderlineOfIllusion databaseGamePvEEventTheBorderlineOfIllusion &&
                gamePlayer instanceof DatabaseGamePlayerPvEEventTheBorderlineOfIllusion databaseGamePlayerPvEEventIllumina
        ) {
            this.borderLineOfIllusionStats.updateStats(databasePlayer,
                    databaseGamePvEEventTheBorderlineOfIllusion,
                    gameMode,
                    databaseGamePlayerPvEEventIllumina,
                    result,
                    multiplier,
                    playersCollection
            );
        }
    }

    public DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats getBorderLineOfIllusionStats() {
        return borderLineOfIllusionStats;
    }

    @Override
    public Collection<? extends PvEEventIlluminaStatsWarlordsClasses<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina>, PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina>>>> getStats() {
        return Stream.of(borderLineOfIllusionStats) // TODO
                     .flatMap(stats -> (Stream<? extends PvEEventIlluminaStatsWarlordsClasses<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina>, PvEEventIlluminaStatsWarlordsSpecs<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina, PvEEventIlluminaStats<DatabaseGamePvEEventIllumina<DatabaseGamePlayerPvEEventIllumina>, DatabaseGamePlayerPvEEventIllumina>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               .stream())
                     .toList();
    }

    @Override
    public long getEventPointsCumulative() {
        return MultiPvEEventIlluminaStats.super.getEventPointsCumulative();
    }
}

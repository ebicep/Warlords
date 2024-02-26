package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePlayerPvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePlayerPvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePlayerPvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.DatabasePlayerPvEEventGardenOfHesperidesTartarusStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats implements MultiPvEEventGardenOfHesperidesStats<
        PvEEventGardenOfHesperidesStatsWarlordsClasses<
                DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>,
                DatabaseGamePlayerPvEEventGardenOfHesperides,
                PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>,
                PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>>,
        DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>,
        DatabaseGamePlayerPvEEventGardenOfHesperides,
        PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>,
        PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>
                , DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>>,
        EventMode {

    @Field("acropolis_stats")
    private DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats acropolisStats = new DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats();
    @Field("tartarus_stats")
    private DatabasePlayerPvEEventGardenOfHesperidesTartarusStats tartarusStats = new DatabasePlayerPvEEventGardenOfHesperidesTartarusStats();

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
        return acropolisStats.getPlays() + tartarusStats.getPlays();
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

    @Override
    public Collection<? extends PvEEventGardenOfHesperidesStatsWarlordsClasses<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>, PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>>> getStats() {
        return Stream.of(acropolisStats, tartarusStats) // TODO
                     .flatMap(stats -> (Stream<? extends PvEEventGardenOfHesperidesStatsWarlordsClasses<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>, PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides, PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerPvEEventGardenOfHesperides>, DatabaseGamePlayerPvEEventGardenOfHesperides>>>>) stats.getStats()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               .stream())
                     .toList();
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventGardenOfHesperides databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventGardenOfHesperides gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (databaseGame instanceof DatabaseGamePvEEventTheAcropolis databaseGamePvEEventTheAcropolis && gamePlayer instanceof DatabaseGamePlayerPvEEventTheAcropolis databaseGamePlayerPvEEventTheAcropolis) {
            acropolisStats.updateStats(databasePlayer, databaseGamePvEEventTheAcropolis, gameMode, databaseGamePlayerPvEEventTheAcropolis, result, multiplier, playersCollection);
        } else if (databaseGame instanceof DatabaseGamePvEEventTartarus databaseGamePvEEventTartarus && gamePlayer instanceof DatabaseGamePlayerPvEEventTartarus databaseGamePlayerPvEEventTartarus) {
            tartarusStats.updateStats(databasePlayer, databaseGamePvEEventTartarus, gameMode, databaseGamePlayerPvEEventTartarus, result, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid game or player type");
        }
    }

    @Override
    public long getEventPointsCumulative() {
        return MultiPvEEventGardenOfHesperidesStats.super.getEventPointsCumulative();
    }

    public DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats getAcropolisStats() {
        return acropolisStats;
    }

    public DatabasePlayerPvEEventGardenOfHesperidesTartarusStats getTartarusStats() {
        return tartarusStats;
    }
}

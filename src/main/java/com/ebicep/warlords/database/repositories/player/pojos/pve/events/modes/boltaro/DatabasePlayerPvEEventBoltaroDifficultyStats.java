package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePlayerPvEEventBoltarosLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabasePlayerPvEEventBoltaroBonanzaStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabasePlayerPvEEventBoltaroLairStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Stream;

public class DatabasePlayerPvEEventBoltaroDifficultyStats implements MultiPvEEventBoltaroStats<
        PvEEventBoltaroStatsWarlordsClasses<
                DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>,
                DatabaseGamePlayerPvEEventBoltaro,
                PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>,
                PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>>,
        DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>,
        DatabaseGamePlayerPvEEventBoltaro,
        PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>,
        PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>>,
        EventMode {

    @Field("bonanza_stats")
    private DatabasePlayerPvEEventBoltaroBonanzaStats bonanzaStats = new DatabasePlayerPvEEventBoltaroBonanzaStats();
    @Field("lair_stats")
    private DatabasePlayerPvEEventBoltaroLairStats lairStats = new DatabasePlayerPvEEventBoltaroLairStats();

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
        return bonanzaStats.getPlays() + lairStats.getPlays();
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
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventBoltaro databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventBoltaro gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (databaseGame instanceof DatabaseGamePvEEventBoltaroBonanza databaseGamePvEEventBoltaroBonanza && gamePlayer instanceof DatabaseGamePlayerPvEEventBoltaroBonanza databaseGamePlayerPvEEventBoltaroBonanza) {
            bonanzaStats.updateStats(databasePlayer, databaseGamePvEEventBoltaroBonanza, gameMode, databaseGamePlayerPvEEventBoltaroBonanza, result, multiplier, playersCollection);
        } else if (databaseGame instanceof DatabaseGamePvEEventBoltaroLair databaseGamePvEEventBoltaroLair && gamePlayer instanceof DatabaseGamePlayerPvEEventBoltarosLair databaseGamePlayerPvEEventBoltarosLair) {
            lairStats.updateStats(databasePlayer, databaseGamePvEEventBoltaroLair, gameMode, databaseGamePlayerPvEEventBoltarosLair, result, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid game or player type");
        }
    }


    @Override
    public Collection<PvEEventBoltaroStatsWarlordsClasses<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>, PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>>> getStats() {
        return Stream.of(bonanzaStats, lairStats)
                     .flatMap(s -> s.getStats()
                                    .stream()
                                    .map(ss -> (PvEEventBoltaroStatsWarlordsClasses<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>, PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro, PvEEventBoltaroStats<DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaro>, DatabaseGamePlayerPvEEventBoltaro>>>) (Object) ss)
                     )
                     .toList();
    }

    @Override
    public long getEventPointsCumulative() {
        return MultiPvEEventBoltaroStats.super.getEventPointsCumulative();
    }

    public DatabasePlayerPvEEventBoltaroDifficultyStats() {
    }

    public DatabasePlayerPvEEventBoltaroBonanzaStats getBonanzaStats() {
        return bonanzaStats;
    }

    public DatabasePlayerPvEEventBoltaroLairStats getLairStats() {
        return lairStats;
    }
}

package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class DatabasePlayerPvEEventDifficultyStats implements PvEEventStatsWarlordsClasses<DatabaseBasePvEEvent, PvEEventStatsWarlordsSpecs<DatabaseBasePvEEvent>>, EventMode {

    private DatabaseMagePvEEvent mage = new DatabaseMagePvEEvent();
    private DatabaseWarriorPvEEvent warrior = new DatabaseWarriorPvEEvent();
    private DatabasePaladinPvEEvent paladin = new DatabasePaladinPvEEvent();
    private DatabaseShamanPvEEvent shaman = new DatabaseShamanPvEEvent();
    private DatabaseRoguePvEEvent rogue = new DatabaseRoguePvEEvent();
    private DatabaseArcanistPvEEvent arcanist = new DatabaseArcanistPvEEvent();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventPlayerCountStats());
        put(2, new DatabasePlayerPvEEventPlayerCountStats());
        put(3, new DatabasePlayerPvEEventPlayerCountStats());
        put(4, new DatabasePlayerPvEEventPlayerCountStats());
    }};
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
    public List<AbstractBounty> getActiveBounties() {
        return activeBounties;
    }

    public DatabasePlayerPvEEventDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense;
        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE PLAYER COUNT STATS
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

    }
    public DatabasePlayerPvEEventPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventPlayerCountStats());
    }

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
}

package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStat;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.pve.onslaught.PouchReward;
import com.ebicep.warlords.pve.Spendable;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabasePlayerOnslaughtStats implements MultiStat<DatabaseGamePvEOnslaught, DatabaseGamePlayerPvEOnslaught> {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEOnslaughtPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEOnslaughtPlayerCountStats());
        put(2, new DatabasePlayerPvEOnslaughtPlayerCountStats());
        put(3, new DatabasePlayerPvEOnslaughtPlayerCountStats());
        put(4, new DatabasePlayerPvEOnslaughtPlayerCountStats());
    }};

    public DatabasePlayerOnslaughtStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEOnslaught databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEOnslaught gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        Map<Spendable, Long> syntheticPouch = gamePlayer.getSyntheticPouch();
        Map<Spendable, Long> aspirantPouch = gamePlayer.getAspirantPouch();
        if (multiplier > 0) {
            LinkedHashMap<Spendable, Long> sortedSyntheticPouch = new LinkedHashMap<>();
            syntheticPouch.entrySet()
                          .stream()
                          .sorted((o1, o2) -> Long.compare(o2.getValue(), o1.getValue()))
                          .forEachOrdered(spendableLongEntry -> sortedSyntheticPouch.put(spendableLongEntry.getKey(), spendableLongEntry.getValue()));
            LinkedHashMap<Spendable, Long> sortedAspirantPouch = new LinkedHashMap<>();
            aspirantPouch.entrySet()
                         .stream()
                         .sorted((o1, o2) -> Long.compare(o2.getValue(), o1.getValue()))
                         .forEachOrdered(spendableLongEntry -> sortedAspirantPouch.put(spendableLongEntry.getKey(), spendableLongEntry.getValue()));
            if (!sortedSyntheticPouch.isEmpty()) {
                databasePlayer.getPveStats().getPouchRewards().add(new PouchReward(sortedSyntheticPouch, PouchReward.PouchType.SYNTHETIC));
            }
            if (!sortedAspirantPouch.isEmpty()) {
                databasePlayer.getPveStats().getPouchRewards().add(new PouchReward(sortedAspirantPouch, PouchReward.PouchType.ASPIRANT));
            }
        } else {
            syntheticPouch.forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount * multiplier));
            aspirantPouch.forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount * multiplier));
        }
    }

    @Override
    public <T extends StatsWarlordsClasses<?, ?, ?, ?>> List<T> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(stats -> (T) stats)
                               .toList();
    }
}

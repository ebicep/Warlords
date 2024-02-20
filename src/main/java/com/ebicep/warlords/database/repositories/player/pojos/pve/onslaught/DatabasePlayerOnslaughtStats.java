package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.pve.onslaught.PouchReward;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerOnslaughtStats extends DatabasePlayerPvEOnslaughtDifficultyStats {

    public DatabasePlayerOnslaughtStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        if (gamePlayer instanceof DatabaseGamePlayerPvEOnslaught onslaughtGamePlayer) {

            Map<Spendable, Long> syntheticPouch = onslaughtGamePlayer.getSyntheticPouch();
            Map<Spendable, Long> aspirantPouch = onslaughtGamePlayer.getAspirantPouch();
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
    }

}

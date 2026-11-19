package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Longest current streaks of Anomaly caches claimed without a Legendary or Sovereign item
 */
public class AnomalyCacheStreakLeaderboard {

    public static final int DISPLAYED_PLACES = 10;
    private static final String STREAK_PATH = "honorifics.cleanAnomalyCacheStreak";

    /**
     * Consumer is called on the main thread
     */
    public static void load(Consumer<List<DatabasePlayer>> consumer) {
        Warlords.newChain()
                .asyncFirst(AnomalyCacheStreakLeaderboard::findLongest)
                .syncLast(consumer::accept)
                .execute();
    }

    private static List<DatabasePlayer> findLongest() {
        if (!DatabaseManager.enabled || DatabaseManager.playerService == null) {
            return Collections.emptyList();
        }
        Query query = new Query(Criteria.where(STREAK_PATH).gt(0))
                .with(Sort.by(Sort.Direction.DESC, STREAK_PATH))
                .limit(DISPLAYED_PLACES);
        try {
            return DatabaseManager.playerService.find(query, PlayersCollections.LIFETIME);
        } catch (Exception e) {
            ChatUtils.MessageType.PLAYER_SERVICE.sendErrorMessage(e);
            return Collections.emptyList();
        }
    }

}

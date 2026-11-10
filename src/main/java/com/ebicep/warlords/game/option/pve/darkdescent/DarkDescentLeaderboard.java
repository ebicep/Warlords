package com.ebicep.warlords.game.option.pve.darkdescent;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.GameEventReward;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public class DarkDescentLeaderboard {

    public static final int REWARDED_PLACES = 3;
    private static final int DISPLAYED_PLACES = 10;
    private static final String PROGRESS_PATH = "pve_stats.dark_descent_progress.";

    public static LinkedHashMap<Spendable, Long> getRewards(int place) {
        return switch (place) {
            case 1 -> new LinkedHashMap<>() {{
                put(Currencies.LEGEND_FRAGMENTS, 500L);
                put(SpendableRandomNewItem.LEGENDARY, 1L);
                put(MobDrop.ZENITH_STAR, 1L);
                put(Currencies.ETHEREUM_CRYSTAL, 5L);
            }};
            case 2 -> new LinkedHashMap<>() {{
                put(Currencies.LEGEND_FRAGMENTS, 250L);
                put(SpendableRandomNewItem.EPIC, 1L);
                put(MobDrop.ZENITH_STAR, 1L);
            }};
            case 3 -> new LinkedHashMap<>() {{
                put(Currencies.LEGEND_FRAGMENTS, 150L);
                put(SpendableRandomNewItem.EPIC, 1L);
            }};
            default -> new LinkedHashMap<>();
        };
    }

    public static String formatTime(int ticks) {
        return StringUtils.formatTimeLeft(ticks / 20);
    }

    /**
     * Fastest completions of the current week, consumer is called on the main thread
     */
    public static void loadCurrentWeek(Consumer<List<DatabasePlayer>> consumer) {
        Warlords.newChain()
                .asyncFirst(() -> findFastest(DateUtil.getCurrentWeekStartEpochDay(), DISPLAYED_PLACES))
                .syncLast(consumer::accept)
                .execute();
    }

    /**
     * Grants the place rewards for the week starting at weekStart, called once when the weekly timing resets
     */
    public static void awardWeeklyRewards(Instant weekStart) {
        long week = DateUtil.getEpochDay(weekStart);
        Warlords.newChain()
                .asyncFirst(() -> findFastest(week, REWARDED_PLACES))
                .syncLast(topPlayers -> {
                    for (int i = 0; i < topPlayers.size(); i++) {
                        awardPlace(topPlayers.get(i), i + 1, weekStart);
                    }
                })
                .execute();
    }

    private static List<DatabasePlayer> findFastest(long week, int limit) {
        Query query = new Query(Criteria.where(PROGRESS_PATH + "week").is(week).and(PROGRESS_PATH + "completed").is(true))
                .with(Sort.by(Sort.Direction.ASC, PROGRESS_PATH + "completion_ticks"))
                .limit(limit);
        return DatabaseManager.playerService.find(query, PlayersCollections.LIFETIME);
    }

    private static void awardPlace(DatabasePlayer leaderboardEntry, int place, Instant weekStart) {
        LinkedHashMap<Spendable, Long> rewards = getRewards(place);
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.playerService.findByUUID(leaderboardEntry.getUuid()))
                .syncLast(optionalDatabasePlayer -> optionalDatabasePlayer.ifPresent(databasePlayer -> {
                    databasePlayer.getPveStats()
                                  .getGameEventRewards()
                                  .add(new GameEventReward(rewards, "Dark Descent Leaderboard #" + place, weekStart.getEpochSecond()));
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    RewardInventory.sendRewardMessage(
                            databasePlayer.getUuid(),
                            Component.text("You placed #" + place + " on last week's Dark Descent leaderboard!", NamedTextColor.DARK_PURPLE)
                    );
                    ChatUtils.MessageType.TIMINGS.sendMessage("Awarded Dark Descent #" + place + " to " + databasePlayer.getName());
                }))
                .execute();
    }

}

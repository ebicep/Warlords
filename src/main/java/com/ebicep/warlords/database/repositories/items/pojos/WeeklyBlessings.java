package com.ebicep.warlords.database.repositories.items.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.RandomCollection;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Document(collection = "Items_Weekly_Blessing")
public class WeeklyBlessings {

    public static WeeklyBlessings currentWeeklyBlessings;

    public static void loadWeeklyBlessings() {
        ChatUtils.MessageType.WEEKLY_BLESSINGS.sendMessage("Loading Weekly Blessings - " + DatabaseTiming.RESET_WEEKLY.get());
        if (DatabaseTiming.RESET_WEEKLY.get()) {
            currentWeeklyBlessings = new WeeklyBlessings();
            onInitialize();
            createNewWeeklyBlessings();
        } else {
            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.weeklyBlessingsService.findAll())
                    .syncLast(weeklyBlessings -> {
                        if (weeklyBlessings.isEmpty()) {
                            currentWeeklyBlessings = new WeeklyBlessings();
                            createNewWeeklyBlessings();
                        } else {
                            currentWeeklyBlessings = weeklyBlessings.get(weeklyBlessings.size() - 1);
                        }
                        onInitialize();
                    })
                    .execute();
        }
    }

    private static void onInitialize() {
        ChatUtils.MessageType.WEEKLY_BLESSINGS.sendMessage("Initialized Weekly Blessings - " + currentWeeklyBlessings);
    }

    private static void createNewWeeklyBlessings() {
        Warlords.newChain()
                .async(() -> DatabaseManager.weeklyBlessingsService.create(currentWeeklyBlessings))
                .execute();
    }

    @Id
    protected String id;
    private Instant week = DateUtil.getResetDateLatestMonday();
    private Map<Integer, Integer> stock = new HashMap<>() {{
        RandomCollection<Integer> tierChances = new RandomCollection<Integer>()
                .add(63, 1)
                .add(20, 2)
                .add(10, 3)
                .add(5, 4)
                .add(2, 5);
        for (int i = 0; i < 9; i++) {
            merge(tierChances.next(), 1, Integer::sum);
        }
    }};
    @Field("player_orders")
    private Map<UUID, Map<Integer, Integer>> playerOrders = new HashMap<>();
    public WeeklyBlessings() {
    }

    @Override
    public String toString() {
        return "WeeklyBlessings{" +
                "week=" + week +
                ", stock=" + stock +
                '}';
    }

    public Map<Integer, Integer> getStock() {
        return stock;
    }

    public Map<UUID, Map<Integer, Integer>> getPlayerOrders() {
        return playerOrders;
    }

    public void addPlayerOrder(UUID uuid, int blessingTier) {
        playerOrders.computeIfAbsent(uuid, k -> new HashMap<>()).merge(blessingTier, 1, Integer::sum);
        DatabaseManager.updateWeeklyBlessings(this);
    }

}

package com.ebicep.warlords.database.repositories.timings.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.util.java.DateUtil;
import com.mongodb.client.MongoCollection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Document(collection = "Timings")
public class DatabaseTiming {

    private static final String[] WEEKLY_EXPERIENCE_LEADERBOARDS = new String[]{
            "Wins",
            "Losses",
            "Kills",
            "Assists",
            "Deaths",
            "DHP",
            "DHP Per Game",
            "Damage",
            "Healing",
            "Absorbed",
            "Flags Captured",
            "Flags Returned",
    };
    @Id
    protected String id;
    private String title;
    @Field("last_reset")
    private Instant lastReset = DateUtil.getResetDateToday();
    @Field("timing")
    private Timing timing;

    public DatabaseTiming() {
    }

    public DatabaseTiming(String title, Timing timing) {
        this.title = title;
        this.timing = timing;
    }

    public DatabaseTiming(String title, Instant lastReset, Timing timing) {
        this.title = title;
        this.lastReset = lastReset;
        this.timing = timing;
    }

    public static void checkTimings() {
        Instant currentDate = Instant.now();
        //WEEKLY
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Weekly Stats"))
                .asyncLast(timing -> {
                    if (timing == null) {
                        System.out.println("[Timings] Could not find Weekly Stats timing in database. Creating new timing.");
                        DatabaseManager.timingsService.create(new DatabaseTiming("Weekly Stats", DateUtil.getResetDateLatestMonday(), Timing.WEEKLY));
                    } else {
                        long minutesBetween = ChronoUnit.MINUTES.between(timing.getLastReset(), currentDate);
                        System.out.println("[Timings] Weekly Reset Time Minute: " + minutesBetween + " > " + (timing.getTiming().minuteDuration - 30));
                        //30 min buffer
                        if (minutesBetween > 0 && minutesBetween > timing.getTiming().minuteDuration - 30) {
                            try {
                                //adding new document with top weekly players
                                org.bson.Document topPlayers = getTopPlayersOnLeaderboard();
                                MongoCollection<org.bson.Document> weeklyLeaderboards = DatabaseManager.warlordsDatabase.getCollection("Weekly_Leaderboards");
                                weeklyLeaderboards.insertOne(topPlayers);

                                ExperienceManager.awardWeeklyExperience(topPlayers);
                                //clearing weekly
                                DatabaseManager.playerService.deleteAll(PlayersCollections.WEEKLY);
                            } catch (Exception e) {
                                System.out.println("[Timings] ERROR DOING WEEKLY EXP THINGY - COMPS DIDNT HAPPEN?");
                            }
                            //updating date to current
                            timing.setLastReset(DateUtil.getResetDateLatestMonday());
                            DatabaseManager.timingsService.update(timing);

                            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Weekly player information reset");
                        }
                    }
                })
                .sync(() -> {
                    //reloading boards
                    StatsLeaderboardManager.CACHED_PLAYERS.get(PlayersCollections.WEEKLY).clear();
                    StatsLeaderboardManager.reloadLeaderboardsFromCache(PlayersCollections.WEEKLY, false);
                })
                .execute();
        //DAILY
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Daily Stats"))
                .asyncLast(timing -> {
                    if (timing == null) {
                        System.out.println("[Timings] Could not find Daily Stats timing in database. Creating new timing.");
                        DatabaseManager.timingsService.create(new DatabaseTiming("Daily Stats", DateUtil.getResetDateToday(), Timing.DAILY));
                    } else {
                        long minutesBetween = ChronoUnit.MINUTES.between(timing.getLastReset(), currentDate);
                        System.out.println("[Timings] Daily Reset Time Minute: " + minutesBetween + " > " + (timing.getTiming().minuteDuration - 30));
                        //30 min buffer
                        if (minutesBetween > 0 && minutesBetween > timing.getTiming().minuteDuration - 30) {
                            //clearing daily
                            DatabaseManager.playerService.deleteAll(PlayersCollections.DAILY);
                            //updating date to current
                            timing.setLastReset(DateUtil.getResetDateToday());
                            DatabaseManager.timingsService.update(timing);
                            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Daily player information reset");
                        }
                    }
                })
                .sync(() -> {
                    //reloading boards
                    StatsLeaderboardManager.CACHED_PLAYERS.get(PlayersCollections.DAILY).clear();
                    StatsLeaderboardManager.reloadLeaderboardsFromCache(PlayersCollections.DAILY, false);
                })
                .execute();
    }

    public static org.bson.Document getTopPlayersOnLeaderboard() {
        List<StatsLeaderboard> statsLeaderboards = StatsLeaderboardManager.LEADERBOARD_CTF.getComps().getLeaderboards();
        org.bson.Document document = new org.bson.Document("date", Instant.now()).append("total_players", statsLeaderboards.get(0).getSortedWeekly().size());
        for (String title : WEEKLY_EXPERIENCE_LEADERBOARDS) {
            statsLeaderboards.stream().filter(leaderboard -> leaderboard.getTitle().equals(title)).findFirst().ifPresent(leaderboard -> {
                Number[] numbers = leaderboard.getTopThreeValues();
                String[] names = leaderboard.getTopThreePlayerNames(numbers, DatabasePlayer::getName);
                String[] uuids = leaderboard.getTopThreePlayerNames(numbers, databasePlayer -> databasePlayer.getUuid().toString());
                List<org.bson.Document> topList = new ArrayList<>();
                for (int i = 0; i < numbers.length; i++) {
                    topList.add(new org.bson.Document("names", names[i]).append("uuids", uuids[i]).append("amount", numbers[i]));
                }
                org.bson.Document totalDocument = new org.bson.Document();
                if (numbers[0] instanceof Integer) {
                    totalDocument = new org.bson.Document("total", Arrays.stream(numbers).mapToInt(Number::intValue).sum());
                } else if (numbers[0] instanceof Long) {
                    totalDocument = new org.bson.Document("total", Arrays.stream(numbers).mapToLong(Number::longValue).sum());
                }
                document.append(title.toLowerCase().replace(" ", "_"), totalDocument.append("name", title).append("top", topList));
            });
        }
        return document;
    }

    public String getTitle() {
        return title;
    }

    public Instant getLastReset() {
        return lastReset;
    }

    public void setLastReset(Instant lastReset) {
        this.lastReset = lastReset;
    }

    public Timing getTiming() {
        return timing;
    }

    @Override
    public String toString() {
        return "DatabaseTiming{" +
                "title='" + title + '\'' +
                ", timing=" + timing +
                '}';
    }
}

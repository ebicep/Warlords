package com.ebicep.warlords.database.repositories.timings.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
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
import java.util.UUID;

@Document(collection = "Timings")
public class DatabaseTiming {

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
                                org.bson.Document topPlayers = LeaderboardManager.getTopPlayersOnLeaderboard();
                                MongoCollection<org.bson.Document> weeklyLeaderboards = DatabaseManager.warlordsDatabase.getCollection("Weekly_Leaderboards");
                                weeklyLeaderboards.insertOne(topPlayers);

                                ExperienceManager.awardWeeklyExperience(topPlayers);
                                //clearing weekly
                                DatabaseManager.playerService.deleteAll(PlayersCollections.WEEKLY);
                                //reloading boards
                                LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString(), false);
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
                .execute();
    }

    public void setLastReset(Instant lastReset) {
        this.lastReset = lastReset;
    }

    public String getTitle() {
        return title;
    }

    public Instant getLastReset() {
        return lastReset;
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

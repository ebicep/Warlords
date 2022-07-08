package com.ebicep.warlords.database.repositories.timings.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.mongodb.client.MongoCollection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.UUID;

@Document(collection = "Timings")
public class DatabaseTiming {

    @Id
    protected String id;
    private String title;
    @Field("last_reset")
    private Date lastReset = new Date();
    @Field("timing")
    private Timing timing;

    public DatabaseTiming() {
    }

    public DatabaseTiming(String title, Timing timing) {
        this.title = title;
        this.timing = timing;
    }

    public static void checkTimings() {
        Date currentDate = new Date();
        //WEEKLY
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Weekly Stats"))
                .abortIfNull()
                .asyncLast(timing -> {
                    long timeDiff = currentDate.getTime() - timing.getLastReset().getTime();
                    long minuteDiff = Timing.millisecondToMinute(timeDiff);

                    System.out.println("Weekly Reset Time Minute: " + minuteDiff);
                    //30 min buffer
                    if (timeDiff > 0 && minuteDiff > timing.getTiming().minuteDuration - 30) {
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
                            System.out.println("ERROR DOING WEEKLY EXP THINGY - COMPS DIDNT HAPPEN?");
                        }
                        //updating date to current
                        timing.reset();
                        DatabaseManager.timingsService.update(timing);
                    }
                })
                .sync(() -> Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Weekly player information reset"))
                .execute();
        //DAILY
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Daily Stats"))
                .abortIfNull()
                .asyncLast(timing -> {
                    long timeDiff = currentDate.getTime() - timing.getLastReset().getTime();
                    long minuteDiff = Timing.millisecondToMinute(timeDiff);

                    System.out.println("Daily Reset Time Minute: " + minuteDiff);
                    //30 min buffer
                    if (timeDiff > 0 && minuteDiff > timing.getTiming().minuteDuration - 30) {
                        //clearing daily
                        DatabaseManager.playerService.deleteAll(PlayersCollections.DAILY);
                        //updating date to current
                        timing.reset();
                        DatabaseManager.timingsService.update(timing);
                    }
                })
                .sync(() -> Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Weekly player information reset"))
                .execute();
    }

    public void reset() {
        lastReset = new Date();
    }

    public String getTitle() {
        return title;
    }

    public Date getLastReset() {
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

package com.ebicep.warlords.database.repositories.timings.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.mongodb.client.MongoCollection;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public static AtomicBoolean resetWeekly = new AtomicBoolean(false);
    public static AtomicBoolean resetDaily = new AtomicBoolean(false);

    public static void checkStatsTimings() {
        Instant currentDate = Instant.now();
        //WEEKLY
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Weekly Stats"))
                .async(timing -> {
                    if (timing == null) {
                        ChatUtils.MessageTypes.TIMINGS.sendMessage("Could not find Weekly Stats timing in database. Creating new timing.");
                        DatabaseManager.timingsService.create(new DatabaseTiming("Weekly Stats", DateUtil.getResetDateLatestMonday(), Timing.WEEKLY));
                    } else {
                        long minutesBetween = ChronoUnit.MINUTES.between(timing.getLastReset(), currentDate);
                        ChatUtils.MessageTypes.TIMINGS.sendMessage("Weekly Reset Time Minute: " + minutesBetween + " > " + (timing.getTiming().minuteDuration - 30));
                        //10 min buffer
                        if (minutesBetween > 0 && minutesBetween > timing.getTiming().minuteDuration - 30) {
                            //updating date to current
                            timing.setLastReset(DateUtil.getResetDateLatestMonday());
                            DatabaseManager.timingsService.update(timing);
                            ChatUtils.MessageTypes.TIMINGS.sendMessage("Weekly information reset");
                            return true;
                        }
                    }
                    return false;
                })
                .syncLast((reset) -> {
                    if (reset) {
                        resetWeekly.set(true);

                        //guilds
                        for (Guild guild : GuildManager.GUILDS) {
                            guild.setExperience(Timing.WEEKLY, 0);
                            guild.getPlayers().forEach(guildPlayer -> {
                                guildPlayer.setCoins(Timing.WEEKLY, 0L);
                                guildPlayer.setExperience(Timing.WEEKLY, 0);
                            });
                            guild.queueUpdate();
                        }
                        GuildLeaderboardManager.recalculateLeaderboard(Timing.WEEKLY);
                    }
                })
                .execute();
        //DAILY
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Daily Stats"))
                .async(timing -> {
                    if (timing == null) {
                        ChatUtils.MessageTypes.TIMINGS.sendMessage("Could not find Daily Stats timing in database. Creating new timing.");
                        DatabaseManager.timingsService.create(new DatabaseTiming("Daily Stats", DateUtil.getResetDateToday(), Timing.DAILY));
                    } else {
                        long minutesBetween = ChronoUnit.MINUTES.between(timing.getLastReset(), currentDate);
                        ChatUtils.MessageTypes.TIMINGS.sendMessage("Daily Reset Time Minute: " + minutesBetween + " > " + (timing.getTiming().minuteDuration - 30));
                        //10 min buffer
                        if (minutesBetween > 0 && minutesBetween > timing.getTiming().minuteDuration - 10) {
                            //updating date to current
                            timing.setLastReset(DateUtil.getResetDateToday());
                            DatabaseManager.timingsService.update(timing);
                            ChatUtils.MessageTypes.TIMINGS.sendMessage("Daily information reset");
                            return true;
                        }
                    }
                    return false;
                })
                .syncLast((reset) -> {
                    if (reset) {
                        resetDaily.set(true);

                        //guilds
                        for (Guild guild : GuildManager.GUILDS) {
                            guild.setExperience(Timing.DAILY, 0);
                            guild.getPlayers().forEach(guildPlayer -> {
                                guildPlayer.setDailyCoinBonusReceived(false);
                                guildPlayer.setDailyCoinsConverted(0);
                                guildPlayer.setCoins(Timing.DAILY, 0L);
                                guildPlayer.setExperience(Timing.DAILY, 0);
                            });
                            guild.queueUpdate();
                        }
                        GuildLeaderboardManager.recalculateLeaderboard(Timing.DAILY);
                    }
                })
                .execute();
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

    public static void checkLeaderboardResets() {
        if (resetWeekly.get()) {
            resetWeekly.set(false);
            try {
                //adding new document with top weekly players
                org.bson.Document topPlayers = getTopPlayersOnLeaderboard();
                MongoCollection<org.bson.Document> weeklyLeaderboards = DatabaseManager.warlordsDatabase.getCollection("Weekly_Leaderboards");
                weeklyLeaderboards.insertOne(topPlayers);

                ExperienceManager.awardWeeklyExperience(topPlayers);
            } catch (Exception e) {
                ChatUtils.MessageTypes.TIMINGS.sendErrorMessage("ERROR DOING WEEKLY EXP THINGY - COMPS DIDNT HAPPEN?");
            }
            try {
                //clearing weekly
                Warlords.newChain()
                        .async(() -> {
                            DatabaseManager.playerService.renameCollection(
                                    PlayersCollections.WEEKLY.collectionName,
                                    PlayersCollections.WEEKLY.collectionName + "_Previous",
                                    true
                            );
                            DatabaseManager.playerService.deleteAll(PlayersCollections.WEEKLY);
                            ChatUtils.MessageTypes.TIMINGS.sendMessage("Stored previous weekly stats and reset current weekly stats");
                        })
                        .execute();
            } catch (Exception e) {
                ChatUtils.MessageTypes.TIMINGS.sendErrorMessage("Error clearing weekly collection");
            }
            //reloading boards
            StatsLeaderboardManager.CACHED_PLAYERS.get(PlayersCollections.WEEKLY).clear();
            StatsLeaderboardManager.reloadLeaderboardsFromCache(PlayersCollections.WEEKLY, false);
        }
        if (resetDaily.get()) {
            resetDaily.set(false);

            try {
                //clearing daily
                Warlords.newChain()
                        .async(() -> {
                            DatabaseManager.playerService.renameCollection(
                                    PlayersCollections.DAILY.collectionName,
                                    PlayersCollections.DAILY.collectionName + "_Previous",
                                    true
                            );
                            DatabaseManager.playerService.deleteAll(PlayersCollections.DAILY);
                            ChatUtils.MessageTypes.TIMINGS.sendMessage("Stored previous daily stats and reset current daily stats");
                        })
                        .execute();
            } catch (Exception e) {
                ChatUtils.MessageTypes.TIMINGS.sendErrorMessage("Error clearing daily collection");
            }
            //reloading boards
            StatsLeaderboardManager.CACHED_PLAYERS.get(PlayersCollections.DAILY).clear();
            StatsLeaderboardManager.reloadLeaderboardsFromCache(PlayersCollections.DAILY, false);
        }
    }

    public static org.bson.Document getTopPlayersOnLeaderboard() {
        List<StatsLeaderboard> statsLeaderboards = StatsLeaderboardManager.STATS_LEADERBOARDS.get(StatsLeaderboardManager.GameType.CTF)
                .getCategories()
                .get(1)
                .getLeaderboards();
        org.bson.Document document = new org.bson.Document("date", Instant.now()).append("total_players",
                statsLeaderboards.get(0).getSortedPlayers(PlayersCollections.WEEKLY).size()
        );
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

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "DatabaseTiming{" +
                "title='" + title + '\'' +
                ", timing=" + timing +
                '}';
    }
}

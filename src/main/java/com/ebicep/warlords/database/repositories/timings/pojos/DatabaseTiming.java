package com.ebicep.warlords.database.repositories.timings.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.illusionvendor.pojos.IllusionVendorWeeklyShop;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.mongodb.client.MongoCollection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    public static final AtomicBoolean RESET_MONTHLY = new AtomicBoolean(false);
    public static final AtomicBoolean RESET_WEEKLY = new AtomicBoolean(false);
    public static final AtomicBoolean RESET_DAILY = new AtomicBoolean(false);

    public static void checkTimings() {
        Instant currentDate = Instant.now();
        //MONTHLY
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Monthly Stats"))
                .async(timing -> {
                    if (timing == null) {
                        ChatUtils.MessageType.TIMINGS.sendMessage("Could not find Monthly Stats timing in database. Creating new timing.");
                        DatabaseManager.timingsService.create(new DatabaseTiming("Monthly Stats", DateUtil.getResetDateCurrentMonth(), Timing.MONTHLY));
                    } else {
                        ZonedDateTime resetTime = timing.getLastReset().atZone(ZoneOffset.UTC);
                        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
                        if (now.getYear() > resetTime.getYear() || now.getMonthValue() > resetTime.getMonthValue()) {
                            //updating date to current
                            timing.setLastReset(DateUtil.getResetDateCurrentMonth());
                            DatabaseManager.timingsService.update(timing);
                            ChatUtils.MessageType.TIMINGS.sendMessage("Monthly information reset");
                            return true;
                        }
                    }
                    return false;
                })
                .syncLast((reset) -> {
                    if (reset) {
                        RESET_MONTHLY.set(true);
                        //guilds
                        for (Guild guild : GuildManager.GUILDS) {
                            guild.setCoins(Timing.MONTHLY, 0);
                            guild.setExperience(Timing.MONTHLY, 0);
                            guild.getPlayers().forEach(guildPlayer -> {
                                guildPlayer.setCoins(Timing.MONTHLY, 0L);
                                guildPlayer.setExperience(Timing.MONTHLY, 0);
                            });
                            guild.queueUpdate();
                        }
                        GuildLeaderboardManager.recalculateLeaderboard(Timing.MONTHLY);
                    }
                })
                .execute();
        //WEEKLY
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Weekly Stats"))
                .async(timing -> {
                    if (timing == null) {
                        ChatUtils.MessageType.TIMINGS.sendMessage("Could not find Weekly Stats timing in database. Creating new timing.");
                        DatabaseManager.timingsService.create(new DatabaseTiming("Weekly Stats", DateUtil.getResetDateLatestMonday(), Timing.WEEKLY));
                    } else {
                        long minutesBetween = ChronoUnit.MINUTES.between(timing.getLastReset(), currentDate);
                        ChatUtils.MessageType.TIMINGS.sendMessage("Weekly Reset Time Minute: " + minutesBetween + " > " + (timing.getTiming().minuteDuration - 30));
                        //10 min buffer
                        if (minutesBetween > 0 && minutesBetween > timing.getTiming().minuteDuration - 30) {
                            //updating date to current
                            timing.setLastReset(DateUtil.getResetDateLatestMonday());
                            DatabaseManager.timingsService.update(timing);
                            ChatUtils.MessageType.TIMINGS.sendMessage("Weekly information reset");
                            return true;
                        }
                    }
                    return false;
                })
                .syncLast((reset) -> {
                    if (reset) {
                        RESET_WEEKLY.set(true);
                        //guilds
                        for (Guild guild : GuildManager.GUILDS) {
                            guild.setCoins(Timing.WEEKLY, 0);
                            guild.setExperience(Timing.WEEKLY, 0);
                            guild.getPlayers().forEach(guildPlayer -> {
                                guildPlayer.setCoins(Timing.WEEKLY, 0L);
                                guildPlayer.setExperience(Timing.WEEKLY, 0);
                            });
                            guild.queueUpdate();
                        }
                        GuildLeaderboardManager.recalculateLeaderboard(Timing.WEEKLY);
                    }

                    IllusionVendorWeeklyShop.loadWeeklyIllusionVendor();
                })
                .execute();
        //DAILY
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Daily Stats"))
                .async(timing -> {
                    if (timing == null) {
                        ChatUtils.MessageType.TIMINGS.sendMessage("Could not find Daily Stats timing in database. Creating new timing.");
                        DatabaseManager.timingsService.create(new DatabaseTiming("Daily Stats", DateUtil.getResetDateToday(), Timing.DAILY));
                    } else {
                        long minutesBetween = ChronoUnit.MINUTES.between(timing.getLastReset(), currentDate);
                        ChatUtils.MessageType.TIMINGS.sendMessage("Daily Reset Time Minute: " + minutesBetween + " > " + (timing.getTiming().minuteDuration - 30));
                        //10 min buffer
                        if (minutesBetween > 0 && minutesBetween > timing.getTiming().minuteDuration - 10) {
                            //updating date to current
                            timing.setLastReset(DateUtil.getResetDateToday());
                            DatabaseManager.timingsService.update(timing);
                            ChatUtils.MessageType.TIMINGS.sendMessage("Daily information reset");
                            return true;
                        }
                    }
                    return false;
                })
                .syncLast((reset) -> {
                    if (reset) {
                        RESET_DAILY.set(true);
                        //guilds
                        for (Guild guild : GuildManager.GUILDS) {
                            guild.setCoins(Timing.DAILY, 0);
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
        if (RESET_MONTHLY.get()) {
            RESET_MONTHLY.set(false);
            try {
                //clearing weekly
                Warlords.newChain()
                        .async(() -> {
                            DatabaseManager.playerService.renameCollection(
                                    PlayersCollections.MONTHLY.collectionName,
                                    PlayersCollections.MONTHLY.collectionName + "_Previous",
                                    true
                            );
                            DatabaseManager.playerService.deleteAll(PlayersCollections.MONTHLY);
                            ChatUtils.MessageType.TIMINGS.sendMessage("Stored previous monthly stats and reset current monthly stats");
                        })
                        .execute();
            } catch (Exception e) {
                ChatUtils.MessageType.TIMINGS.sendErrorMessage("Error clearing monthly collection");
            }
            //reloading boards
            DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.MONTHLY).clear();
            DatabaseManager.clearQueue(PlayersCollections.MONTHLY);
            for (Player player : Bukkit.getOnlinePlayers()) {
                DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.MONTHLY).put(player.getUniqueId(), new DatabasePlayer(player.getUniqueId(), player.getName()));
            }
            Warlords.newChain()
                    .delay(10 * 20)
                    .sync(() -> StatsLeaderboardManager.resetLeaderboards(PlayersCollections.MONTHLY, null)).execute();
        }
        if (RESET_WEEKLY.get()) {
            RESET_WEEKLY.set(false);
            try {
                //adding new document with top weekly players
                org.bson.Document topPlayers = getTopPlayersOnLeaderboard();
                MongoCollection<org.bson.Document> weeklyLeaderboards = DatabaseManager.warlordsDatabase.getCollection("Weekly_Leaderboards");
                weeklyLeaderboards.insertOne(topPlayers);

                ExperienceManager.awardWeeklyExperience(topPlayers);
            } catch (Exception e) {
                ChatUtils.MessageType.TIMINGS.sendErrorMessage("ERROR DOING WEEKLY EXP THINGY - COMPS DIDNT HAPPEN?");
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
                            ChatUtils.MessageType.TIMINGS.sendMessage("Stored previous weekly stats and reset current weekly stats");
                        })
                        .execute();
            } catch (Exception e) {
                ChatUtils.MessageType.TIMINGS.sendErrorMessage("Error clearing weekly collection");
            }
            //reloading boards
            DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.WEEKLY).clear();
            DatabaseManager.clearQueue(PlayersCollections.WEEKLY);
            for (Player player : Bukkit.getOnlinePlayers()) {
                DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.WEEKLY).put(player.getUniqueId(), new DatabasePlayer(player.getUniqueId(), player.getName()));
            }
            Warlords.newChain()
                    .delay(20)
                    .sync(() -> StatsLeaderboardManager.resetLeaderboards(PlayersCollections.WEEKLY, null)).execute();
        }
        if (RESET_DAILY.get()) {
            RESET_DAILY.set(false);

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
                            ChatUtils.MessageType.TIMINGS.sendMessage("Stored previous daily stats and reset current daily stats");
                        })
                        .execute();
            } catch (Exception e) {
                ChatUtils.MessageType.TIMINGS.sendErrorMessage("Error clearing daily collection");
            }
            //reloading boards
            DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.DAILY).clear();
            DatabaseManager.clearQueue(PlayersCollections.DAILY);
            for (Player player : Bukkit.getOnlinePlayers()) {
                DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.DAILY).put(player.getUniqueId(), new DatabasePlayer(player.getUniqueId(), player.getName()));
            }
            Warlords.newChain()
                    .delay(20)
                    .sync(() -> StatsLeaderboardManager.resetLeaderboards(PlayersCollections.DAILY, null)).execute();
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

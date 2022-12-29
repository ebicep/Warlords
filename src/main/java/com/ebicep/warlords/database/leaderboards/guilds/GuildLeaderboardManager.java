package com.ebicep.warlords.database.leaderboards.guilds;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.util.java.NumberFormat;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

public class GuildLeaderboardManager {

    public static final HashMap<Timing, TreeSet<Guild>> EXPERIENCE_LEADERBOARD = new HashMap<>() {{
        for (Timing value : Timing.VALUES) {
            put(value, new TreeSet<>((g1, g2) -> Long.compare(g2.getExperience(value), g1.getExperience(value))));
        }
    }};
    public static final HashMap<Timing, TreeSet<Guild>> COINS_LEADERBOARD = new HashMap<>() {{
        for (Timing value : Timing.VALUES) {
            put(value, new TreeSet<>((g1, g2) -> Long.compare(g2.getCoins(value), g1.getCoins(value))));
        }
    }};
    public static final Location EVENT_LEADERBOARD_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -2539.5, 55, 737.5);
    public static final List<Hologram> EVENT_LEADERBOARDS = new ArrayList<>();

    public static void recalculateAllLeaderboards() {
        EXPERIENCE_LEADERBOARD.forEach((timing, guilds) -> {
            guilds.clear();
            guilds.addAll(GuildManager.GUILDS);
        });
        COINS_LEADERBOARD.forEach((timing, guilds) -> {
            guilds.clear();
            guilds.addAll(GuildManager.GUILDS);
        });
        resetEventBoards();
    }

    public static void recalculateLeaderboard(Timing timing) {
        EXPERIENCE_LEADERBOARD.get(timing).clear();
        EXPERIENCE_LEADERBOARD.get(timing).addAll(GuildManager.GUILDS);
        COINS_LEADERBOARD.get(timing).clear();
        COINS_LEADERBOARD.get(timing).addAll(GuildManager.GUILDS);
    }

    public static String getLeaderboardList(TreeSet<Guild> leaderboard, String leaderboardName, Function<Guild, Number> valueFunction) {
        StringBuilder stringBuilder = new StringBuilder(ChatColor.GREEN + "Guild " + leaderboardName + " Leaderboards\n");

        int index = 0;
        for (Guild guild : leaderboard) {
            stringBuilder.append(ChatColor.GRAY).append(index + 1).append(". ")
                         .append(ChatColor.GOLD).append(guild.getName())
                         .append(ChatColor.GRAY).append(" - ")
                         .append(ChatColor.GREEN).append(NumberFormat.addCommaAndRound(valueFunction.apply(guild).doubleValue()))
                         .append("\n");
            index++;
        }

        return stringBuilder.toString();
    }

    public static void resetEventBoards() {
//        EVENT_LEADERBOARDS.forEach(Hologram::delete);
//        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
//        if (currentGameEvent == null) {
//            return;
//        }
//        GameEvents event = currentGameEvent.getEvent();
//        long startDateSecond = currentGameEvent.getStartDateSecond();
//
//        ArrayList<Guild> sortedGuilds = new ArrayList<>(GuildManager.GUILDS);
//        sortedGuilds.sort((o1, o2) -> o2.getEventStats().getOrDefault(event, new HashMap<>()).getOrDefault(startDateSecond, 0L)
//                                        .compareTo(o1.getEventStats().getOrDefault(event, new HashMap<>()).getOrDefault(startDateSecond, 0L)));
//
//        Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(EVENT_LEADERBOARD_LOCATION);
//        HologramLines hologramLines = hologram.getLines();
//        hologramLines.appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Guild Event Points");
//        hologramLines.appendText(ChatColor.GRAY + event.name);
//        for (int i = 0, sortedGuildsSize = sortedGuilds.size(); i < sortedGuildsSize; i++) {
//            Guild guild = sortedGuilds.get(i);
//            hologramLines.appendText(ChatColor.YELLOW.toString() + (i + 1) + ". " +
//                    ChatColor.AQUA + guild.getName() +
//                    ChatColor.GRAY + " - " + ChatColor.YELLOW + NumberFormat.addCommas(guild.getEventStats().getOrDefault(event, new HashMap<>()).getOrDefault(startDateSecond, 0L))
//            );
//        }
//
//        EVENT_LEADERBOARDS.add(hologram);
    }

}

package com.ebicep.warlords.database.leaderboards.guilds;

import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.TreeSet;

public class GuildLeaderboardManager {

    public static final HashMap<Timing, TreeSet<Guild>> EXPERIENCE_LEADERBOARD = new HashMap<Timing, TreeSet<Guild>>() {{
        for (Timing value : Timing.VALUES) {
            put(value, new TreeSet<>((g1, g2) -> Long.compare(g2.getExperience(value), g1.getExperience(value))));
        }
    }};
    public static final HashMap<Timing, TreeSet<Guild>> COINS_LEADERBOARD = new HashMap<Timing, TreeSet<Guild>>() {{
        for (Timing value : Timing.VALUES) {
            put(value, new TreeSet<>((g1, g2) -> Long.compare(g2.getCoins(value), g1.getCoins(value))));
        }
    }};

    public static void recalculateAllLeaderboards() {
        EXPERIENCE_LEADERBOARD.forEach((timing, guilds) -> {
            guilds.clear();
            guilds.addAll(GuildManager.GUILDS);
        });
        COINS_LEADERBOARD.forEach((timing, guilds) -> {
            guilds.clear();
            guilds.addAll(GuildManager.GUILDS);
        });
    }

    public static void recalculateLeaderboard(Timing timing) {
        EXPERIENCE_LEADERBOARD.get(timing).clear();
        EXPERIENCE_LEADERBOARD.get(timing).addAll(GuildManager.GUILDS);
        COINS_LEADERBOARD.get(timing).clear();
        COINS_LEADERBOARD.get(timing).addAll(GuildManager.GUILDS);
    }

    public static String getLeaderboardList(TreeSet<Guild> leaderboard, String leaderboardName) {
        StringBuilder stringBuilder = new StringBuilder(ChatColor.GREEN + "Guild " + leaderboardName + " Leaderboards\n");

        int index = 0;
        for (Guild guild : leaderboard) {
            //TODO show value
            stringBuilder.append(ChatColor.GRAY).append(index).append(". ").append(ChatColor.GOLD).append(guild.getName()).append("\n");
            index++;
        }

        return stringBuilder.toString();
    }

}

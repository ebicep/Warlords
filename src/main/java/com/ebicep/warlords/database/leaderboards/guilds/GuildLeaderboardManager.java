package com.ebicep.warlords.database.leaderboards.guilds;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import org.bukkit.ChatColor;

import java.util.TreeSet;

public class GuildLeaderboardManager {

    public static final TreeSet<Guild> DAILY_EXP = new TreeSet<>((g1, g2) -> Long.compare(g2.getDailyExperience(), g1.getDailyExperience()));
    public static final TreeSet<Guild> DAILY_COINS = new TreeSet<>((g1, g2) -> Long.compare(g2.getDailyCoins(), g1.getDailyCoins()));

    public static void recalculateLeaderboards() {
        DAILY_EXP.clear();
        DAILY_COINS.clear();

        DAILY_EXP.addAll(GuildManager.GUILDS);
        DAILY_COINS.addAll(GuildManager.GUILDS);
    }

    public static String getLeaderboardList(TreeSet<Guild> leaderboard, String leaderboardName) {
        StringBuilder stringBuilder = new StringBuilder(ChatColor.GREEN + "Guild " + leaderboardName + " Leaderboards\n");

        int index = 0;
        for (Guild guild : leaderboard) {
            stringBuilder.append(ChatColor.GRAY).append(index).append(". ").append(ChatColor.GOLD).append(guild.getName()).append("\n");
            index++;
        }

        return stringBuilder.toString();
    }

}

package com.ebicep.warlords.database.leaderboards.guilds;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuildLeaderboardManager {

    public static List<Guild> guildsSortedByExp = new ArrayList<>();
    public static List<Guild> guildsSortedByCoins = new ArrayList<>();

    public static void recalculateLeaderboards() {
        guildsSortedByExp.clear();
        guildsSortedByCoins.clear();

        guildsSortedByExp = GuildManager.GUILDS.stream()
                .sorted((o1, o2) -> Long.compare(o2.getExperience(), o1.getExperience()))
                .collect(Collectors.toList());
        guildsSortedByCoins = GuildManager.GUILDS.stream()
                .sorted((o1, o2) -> Long.compare(o2.getCoins(), o1.getCoins()))
                .collect(Collectors.toList());
    }

    public static String getLeaderboardList(List<Guild> leaderboard, String leaderboardName) {
        StringBuilder stringBuilder = new StringBuilder(ChatColor.GREEN + "Guild " + leaderboardName + " Leaderboards");

        for (int i = 0; i < leaderboard.size(); i++) {
            Guild guild = leaderboard.get(i);
            stringBuilder.append(ChatColor.GRAY).append(i).append(". ").append(ChatColor.GOLD).append(guild.getName());
        }

        return stringBuilder.toString();
    }

}

package com.ebicep.warlords.database.leaderboards;

import com.ebicep.warlords.Warlords;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class LeaderboardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.leaderboard.interaction")) {
            return true;
        }

        if(args.length < 1) {
            return true;
        }

        String input = args[0];
        switch (input) {
            case "toggle":
                LeaderboardManager.enabled = !LeaderboardManager.enabled;
                LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString(), false);
                if (LeaderboardManager.enabled) {
                    sender.sendMessage(ChatColor.GREEN + "Leaderboards enabled");
                } else {
                    sender.sendMessage(ChatColor.RED + "Leaderboards disabled");
                }
                return true;
            case "reload":
                LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString(), false);
                sender.sendMessage(ChatColor.GREEN + "Leaderboards reloaded");
                return true;
            case "refresh":
                Bukkit.getOnlinePlayers().forEach(LeaderboardManager::setLeaderboardHologramVisibility);
                sender.sendMessage(ChatColor.GREEN + "Refreshed visibility for all players");
                return true;
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("lb").setExecutor(this);
    }
}

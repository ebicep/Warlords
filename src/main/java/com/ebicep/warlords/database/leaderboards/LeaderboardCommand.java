package com.ebicep.warlords.database.leaderboards;

import com.ebicep.warlords.Warlords;
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
                if(LeaderboardManager.enabled) {
                    sender.sendMessage(ChatColor.RED + "Leaderboards disabled");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Leaderboards enabled");
                }
                LeaderboardManager.enabled = !LeaderboardManager.enabled;
                LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString());
                return true;
            case "reload":
                sender.sendMessage(ChatColor.GREEN + "Leaderboards reloaded");
                LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString());
                return true;
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("lb").setExecutor(this);
    }
}

package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class LeaderboardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.isOp()) {
            return true;
        }

        if(args.length < 1) {
            return true;
        }

        String input = args[0];
        switch (input) {
            case "toggle":
                if(Leaderboards.enabled) {
                    sender.sendMessage(ChatColor.RED + "Leaderboards disabled");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Leaderboards enabled");
                }
                Leaderboards.enabled = !Leaderboards.enabled;
                Leaderboards.addHologramLeaderboards(UUID.randomUUID().toString());
                return true;
            case "reload":
                sender.sendMessage(ChatColor.GREEN + "Leaderboards reloaded");
                Leaderboards.addHologramLeaderboards(UUID.randomUUID().toString());
                return true;
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("lb").setExecutor(this);
    }
}

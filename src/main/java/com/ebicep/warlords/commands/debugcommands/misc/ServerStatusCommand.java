package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.Warlords;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ServerStatusCommand implements CommandExecutor {

    public static boolean enabled = true;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
            return true;
        }

        if (args.length == 0) {
            return true;
        }

        switch (args[0]) {
            case "toggle": {
                enabled = !enabled;
                if (enabled) {
                    sender.sendMessage(ChatColor.GREEN + "Server status is now enabled.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Server status is now disabled.");
                }
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("serverstatus").setExecutor(this);
    }

}

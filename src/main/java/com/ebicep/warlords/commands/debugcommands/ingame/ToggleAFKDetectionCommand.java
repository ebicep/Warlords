package com.ebicep.warlords.commands.debugcommands.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.option.AFKDetectionOption;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleAFKDetectionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
            return true;
        }

        if (args.length == 0) {
            return true;
        }

        String arg = args[0];
        switch (arg) {
            case "enable":
                AFKDetectionOption.enabled = true;
                sender.sendMessage(ChatColor.GREEN + "AFK detection is now enabled.");
                break;
            case "disable":
                AFKDetectionOption.enabled = false;
                sender.sendMessage(ChatColor.RED + "AFK detection is now disabled.");
                break;
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("afkdetection").setExecutor(this);
    }

}

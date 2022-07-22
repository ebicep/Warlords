package com.ebicep.warlords.commands.debugcommands.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.option.GameFreezeWhenOfflineOption;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleOfflineFreezeCommand implements CommandExecutor {

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
                GameFreezeWhenOfflineOption.enabled = true;
                sender.sendMessage(ChatColor.GREEN + "Offline Freeze is now enabled.");
                break;
            case "disable":
                GameFreezeWhenOfflineOption.enabled = false;
                sender.sendMessage(ChatColor.RED + "Offline Freeze is now disabled.");
                break;
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("offlinefreeze").setExecutor(this);
    }
}

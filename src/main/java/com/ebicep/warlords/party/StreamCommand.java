package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StreamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        if (Warlords.partyManager.inAParty(((Player) sender).getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are already in a party");
            return true;
        }

        if (args.length == 0) {
            Warlords.partyManager.getParties().add(new Party(((Player) sender).getUniqueId(), true));
            sender.sendMessage(ChatColor.GREEN + "You created an open party!");
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("stream").setExecutor(this);
    }

}

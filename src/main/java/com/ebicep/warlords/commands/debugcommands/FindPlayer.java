package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FindPlayer implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Insufficient Permissions!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Invalid Arguments! Enter player name");
            return true;
        }

        String targetPlayer = args[0];
        Player player = Bukkit.getPlayer(targetPlayer);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "That player does not exist!");
            return true;
        }

        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

        if (warlordsPlayer == null) {
            sender.sendMessage(ChatColor.RED + "That player is not in a game!");
            return true;
        }

        sender.sendMessage(ChatColor.GREEN + player.getName() + " is in game - " + warlordsPlayer.getGame().getGameId().toString());


        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("findplayer").setExecutor(this);
    }
}

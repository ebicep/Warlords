package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.maps.GameManager.GameHolder;
import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameKillCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.end")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }
        
        List<GameHolder> gameInstances;
        if (args.length == 0) {
            WarlordsPlayer wp = BaseCommand.requireWarlordsPlayer(sender);
            if(wp == null) {
                return true;
            }
            gameInstances = Warlords.getGameManager().getGames().stream().filter(e -> e.getGame() == wp.getGame()).collect(Collectors.toList());
            if (gameInstances.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Unable to find the game that your are in!");
            }
        } else {
            List<String> a = Arrays.asList(args);
            gameInstances = Warlords.getGameManager().getGames().stream().filter(e -> a.contains(e.getName())).collect(Collectors.toList());
        }
        
        for(GameHolder holder : gameInstances) {
            if (holder.getGame() == null) {
                sender.sendMessage(ChatColor.RED + "[" + holder.getName() + "] The game is not active now");
                continue;
            }
            holder.forceEndGame();
            sender.sendMessage(ChatColor.RED + "[" + holder.getName() + "] Terminated");
        }

        sender.sendMessage(ChatColor.RED + "Game has been killed. Warping back to lobby...");

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("killgame").setExecutor(this);
    }
}

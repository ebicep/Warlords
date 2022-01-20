package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameAddon;
import com.ebicep.warlords.maps.GameManager.GameHolder;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.maps.state.PreLobbyState;
import java.util.EnumSet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameListCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.list")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }
        
        for(GameHolder holder : Warlords.getGameManager().getGames()) {
            StringBuilder message = new StringBuilder();
            message
                    .append(ChatColor.GRAY).append("[")
                    .append(ChatColor.AQUA).append(holder.getName())
                    .append(ChatColor.GRAY).append("|")
                    .append(ChatColor.AQUA).append(holder.getMap().name());
            Game game = holder.getGame();
            if (game == null) {
                message.append(ChatColor.GOLD).append(" <inactive>");
            } else {
                if (holder.getMap().getCategories().size() > 1) {
                    message.append(ChatColor.GRAY).append("/").append(ChatColor.AQUA).append(game.getCategory());
                }
                message.append(ChatColor.GRAY).append("] ");
                EnumSet<GameAddon> addons = game.getAddons();
                if (!addons.isEmpty()) {
                    message.append(ChatColor.GRAY).append('(');
                    for(GameAddon addon : addons) {
                        message
                                .append(ChatColor.GREEN).append(addon.name())
                                .append(ChatColor.GRAY).append(',');
                    }
                    message.setLength(message.length() - 1);
                    message.append("] ");
                }
                message
                        .append(ChatColor.GOLD).append(game.getState().getClass().getSimpleName())
                        .append(ChatColor.GRAY).append(" [")
                        .append(ChatColor.GREEN).append(game.getPlayers().size())
                        .append(ChatColor.GRAY).append("/")
                        .append(ChatColor.GREEN).append(game.getMinPlayers())
                        .append(ChatColor.GRAY).append("/")
                        .append(ChatColor.GREEN).append(game.getMaxPlayers())
                        .append(ChatColor.GRAY).append("]");
                }
            sender.sendMessage(message.toString());
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("gamelist").setExecutor(this);
    }
}

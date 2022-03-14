package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.game.option.WinAfterTimeoutOption;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.EnumSet;
import java.util.OptionalInt;

import static com.ebicep.warlords.util.warlords.Utils.toTitleHumanCase;

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
                    .append(ChatColor.AQUA).append(toTitleHumanCase(holder.getMap().name()));
            Game game = holder.getGame();
            if (game == null) {
                message.append(']').append(ChatColor.GOLD).append(" <inactive>");
            } else {
                if (holder.getMap().getCategories().size() > 1) {
                    message.append(ChatColor.GRAY).append("/").append(ChatColor.AQUA).append(toTitleHumanCase(game.getGameMode()));
                }
                message.append(ChatColor.GRAY).append("] ");
                //message.append('(').append(ChatColor.GOLD).append(game.getGameId()).append(ChatColor.GRAY).append(") ");
                EnumSet<GameAddon> addons = game.getAddons();
                if (!addons.isEmpty()) {
                    message.append(ChatColor.GRAY).append('(');
                    for(GameAddon addon : addons) {
                        message.append(ChatColor.GREEN).append(addon.name());
                        message.append(ChatColor.GRAY).append(',');
                    }
                    message.setLength(message.length() - 1);
                    message.append("] ");
                }
                message
                        .append(ChatColor.GOLD).append(game.getState().getClass().getSimpleName())
                        .append(ChatColor.GRAY).append(" [ ")
                        .append(ChatColor.GREEN).append(game.getPlayers().size())
                        .append(ChatColor.GRAY).append("/")
                        .append(ChatColor.GREEN).append(game.getMinPlayers())
                        .append(ChatColor.GRAY).append("..")
                        .append(ChatColor.GREEN).append(game.getMaxPlayers())
                        .append(ChatColor.GRAY).append("] ");
                OptionalInt timeLeft = WinAfterTimeoutOption.getTimeLeft(game);
                String time = Utils.formatTimeLeft(timeLeft.isPresent() ? timeLeft.getAsInt() : (System.currentTimeMillis() - game.createdAt()) / 1000);
                String word = timeLeft.isPresent() ? " Left" : " Elapsed";
                message.append(time).append(word);
            }
                    
            sender.sendMessage(message.toString());
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("gamelist").setExecutor(this);
    }
}

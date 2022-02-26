package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase.previousGames;


public class GamesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.lookupgame")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        if (args.length == 0) {
            StringBuilder stringBuilder = new StringBuilder(ChatColor.GREEN + "Previous Games - \n");
            for (int i = 0; i < previousGames.size(); i++) {
                stringBuilder.append(ChatColor.YELLOW).append(i).append(". ").append(previousGames.get(i).getGameLabel()).append("\n");
            }
            sender.sendMessage(stringBuilder.toString());
            return true;
        } else {
            if (args[0].equals("reload")) {
                sender.sendMessage(ChatColor.GREEN + "Deleting Holograms");
                previousGames.forEach(DatabaseGameBase::deleteHolograms);
                sender.sendMessage(ChatColor.GREEN + "Creating Holograms");
                previousGames.forEach(DatabaseGameBase::createHolograms);
                sender.sendMessage(ChatColor.GREEN + "Setting Visibility");
                Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility);
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Invalid Arguments! [add/remove] [gameNumber]");
                return true;
            }

            if (!NumberUtils.isNumber(args[1])) {
                sender.sendMessage(ChatColor.RED + "Invalid game number!");
                return true;
            }

            int gameNumber = Integer.parseInt(args[1]);
            if (gameNumber >= previousGames.size() || gameNumber < 0) {
                sender.sendMessage(ChatColor.RED + "Invalid game number!");
                return true;
            }

            String input = args[0];
            switch (input.toLowerCase()) {
                case "add":
                    DatabaseGameBase.addGameToDatabase(previousGames.get(gameNumber));
                    sender.sendMessage(ChatColor.GREEN + "Adding game!");
                    return true;
                case "remove":
                    DatabaseGameBase.removeGameFromDatabase(previousGames.get(gameNumber));
                    sender.sendMessage(ChatColor.RED + "Removing game!");
                    return true;
            }
        }


        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("games").setExecutor(this);
    }

}

package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }

        Optional<Game> currentGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
        if (!currentGame.isPresent()) {
            player.sendMessage(ChatColor.RED + "You are not in a game");
            return true;
        }
        Game game = currentGame.get();
        Team playerTeam = game.getPlayerTeam(player.getUniqueId());
        if (playerTeam != null && !currentGame.get().acceptsPeople()) {
            player.sendMessage(ChatColor.RED + "The game does not allow people to leave at the moment, you can only leave public games when in the lobby.");
        } else {
            game.removePlayer(player.getUniqueId());
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("lobby").setExecutor(this);
    }

}

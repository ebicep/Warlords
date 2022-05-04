package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }

        Optional<Game> currentGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
        if (!currentGame.isPresent()) {
            player.sendMessage(ChatColor.RED + "You are not in a game.");
            return true;
        }
        Game game = currentGame.get();
        Team playerTeam = game.getPlayerTeam(player.getUniqueId());
        if (playerTeam != null && !currentGame.get().acceptsPeople()) {
            player.sendMessage(
                    ChatColor.RED + "This command is only enabled in public games. Did you mean to end your private game? Use the command: " +
                    ChatColor.GOLD + "/endprivategame" +
                    ChatColor.RED + "."
            );
        } else {
            game.removePlayer(player.getUniqueId());
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("lobby").setExecutor(this);
    }

}

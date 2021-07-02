package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.maps.state.PreLobbyState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EndgameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }
        Game game = Warlords.game; // In the future allow the user to select a game player
        if (game.getState() instanceof PreLobbyState) {
            sender.sendMessage(ChatColor.RED + "There are no games currently running!");
            return true;
        }

        if (game.getState() instanceof PlayingState) {
            PlayingState playingState = (PlayingState) game.getState();
            playingState.endGame();
        }

        sender.sendMessage(ChatColor.RED + "Game has been terminated. Warping back to lobby...");

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("endgame").setExecutor(this);
    }
}

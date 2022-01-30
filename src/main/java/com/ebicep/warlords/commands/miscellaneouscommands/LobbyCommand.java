package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameAddon;
import com.ebicep.warlords.maps.state.PreLobbyState;
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
        if (currentGame.isPresent() && !currentGame.get().isState(PreLobbyState.class)) {
            if (currentGame.get().getAddons().contains(GameAddon.PRIVATE_GAME)) {
                player.sendMessage(ChatColor.RED + "You cannot leave private games!");
            } else {
                currentGame.get().removePlayer(player.getUniqueId());
            }
        } else {
            player.sendMessage(ChatColor.RED + "This command can only be used in a game lobby!");
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("lobby").setExecutor(this);
    }

}

package com.ebicep.warlords.commands.debugcommands.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class PrivateGameTerminateCommand extends GameTargetCommand implements TabExecutor {

    @Override
    protected void doAction(CommandSender sender, Collection<GameManager.GameHolder> gameInstances) {

        if (gameInstances.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No valid targets found!");
            return;
        }

        for (GameManager.GameHolder holder : gameInstances) {
            Game game = holder.getGame();

            if (game == null) {
                sender.sendMessage(ChatColor.GRAY + "- " + holder.getName() + ": " + ChatColor.RED + "The game is not active now");
                continue;
            }

            Player player = (Player) sender;
            Optional<Party> currentParty = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
            if (game.getAddons().contains(GameAddon.CUSTOM_GAME)) {
                if (currentParty.isPresent()) {
                    Player partyLeader = Bukkit.getPlayer(currentParty.get().getPartyLeader().getUuid());
                    if (partyLeader.getPlayer() != null && partyLeader.getPlayer() == player) {
                        endGameInstance(sender, holder, game);
                        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.RED + "Game has been terminated. Warping back to lobby...");
                    } else {
                        sender.sendMessage(ChatColor.RED + "DEV:" + ChatColor.GRAY + " You are not the party leader, unable to terminate game.");
                    }
                } else {
                    int gamePlayers = 0;
                    for (UUID uuid : game.getPlayers().keySet()) {
                        gamePlayers++;
                    }
                    if (gamePlayers > 1) {
                        sender.sendMessage(ChatColor.RED + "DEV:" + ChatColor.GRAY + " You are not the only player in the game, unable to terminate game.");
                    } else {
                        endGameInstance(sender, holder, game);
                        sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.RED + "Game has been terminated. Warping back to lobby...");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "DEV:" + ChatColor.GRAY + " Game is not private, unable to terminate game.");
            }
        }
    }

    private void endGameInstance(CommandSender sender, GameManager.GameHolder holder, Game game) {
        if (holder.getGame() == null) return;

        if (holder.getGame().isFrozen()) {
            holder.getGame().clearFrozenCause();
        }
        Optional<PlayingState> state = game.getState(PlayingState.class);
        if (!state.isPresent()) {
            sender.sendMessage(ChatColor.GRAY + "- " + holder.getName() + ": " + ChatColor.RED + "The game is not in playing state, instead it is in " + game.getState().getClass().getSimpleName());
        } else {
            sender.sendMessage(ChatColor.GRAY + "- " + holder.getName() + ": " + ChatColor.RED + "Terminating game...");
            game.setNextState(new EndState(game, null));
        }
    }

    public void register(Warlords instance) {
        instance.getCommand("endprivategame").setExecutor(this);
        instance.getCommand("endprivategame").setTabCompleter(this);
    }
}

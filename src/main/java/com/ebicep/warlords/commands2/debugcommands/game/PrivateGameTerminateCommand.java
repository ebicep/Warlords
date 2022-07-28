package com.ebicep.warlords.commands2.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@CommandAlias("endprivategame")
public class PrivateGameTerminateCommand extends BaseCommand {

    private static void endGameInstance(Player player, GameManager.GameHolder holder, Game game) {
        if (holder.getGame() == null) return;

        if (holder.getGame().isFrozen()) {
            holder.getGame().clearFrozenCauses();
        }
        Optional<PlayingState> state = game.getState(PlayingState.class);
        if (!state.isPresent()) {
            player.sendMessage(ChatColor.RED + "The game is not in playing state, instead it is in " + game.getState().getClass().getSimpleName());
        } else {
            player.sendMessage(ChatColor.RED + "Terminating game...");
            game.setNextState(new EndState(game, null));
        }
    }

    @Default
    @Description("Terminates your current game if private")
    public void endPrivateGame(@Conditions("requireWarlordsPlayer|requireGame:withAddon=PRIVATE_GAME") Player player) {
        Game game = Warlords.getPlayer(player).getGame();
        for (GameManager.GameHolder gameHolder : Warlords.getGameManager().getGames()) {
            if (Objects.equals(gameHolder.getGame(), game)) {
                Optional<Party> currentParty = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
                if (currentParty.isPresent()) {
                    Player partyLeader = Bukkit.getPlayer(currentParty.get().getPartyLeader().getUuid());
                    if (partyLeader.getPlayer() != null && partyLeader.getPlayer() == player) {
                        endGameInstance(player, gameHolder, game);
                        player.sendMessage(ChatColor.GREEN + "Game has been terminated. Warping back to lobby...");
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not the party leader, unable to terminate game.");
                    }
                } else {
                    int gamePlayers = 0;
                    // Remove dummies in case of Practice map
                    Warlords.removePlayer(UUID.fromString("8b41f2a4-4a0e-3012-b77b-c2dede582103"));
                    Warlords.removePlayer(UUID.fromString("503adef4-fa6f-4b1b-87bf-cb755e4feb40"));
                    game.removePlayer(UUID.fromString("8b41f2a4-4a0e-3012-b77b-c2dede582103"));
                    game.removePlayer(UUID.fromString("503adef4-fa6f-4b1b-87bf-cb755e4feb40"));

                    for (UUID ignored : game.getPlayers().keySet()) {
                        gamePlayers++;
                    }

                    if (gamePlayers > 1) {
                        player.sendMessage(ChatColor.RED + "You are not the only player in the game, unable to terminate game.");
                    } else {
                        endGameInstance(player, gameHolder, game);
                        player.sendMessage(ChatColor.GREEN + "Game has been terminated. Warping back to lobby...");
                    }
                }

                return;
            }
        }
    }

}


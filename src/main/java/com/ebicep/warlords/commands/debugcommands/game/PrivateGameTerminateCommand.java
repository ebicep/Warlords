package com.ebicep.warlords.commands.debugcommands.game;

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
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Objects;

@CommandAlias("endprivategame")
public class PrivateGameTerminateCommand extends BaseCommand {

    @Default
    @Description("Terminates your current game if private")
    public void endPrivateGame(@Conditions("requireGame:withAddon=PRIVATE_GAME") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (GameManager.GameHolder gameHolder : Warlords.getGameManager().getGames()) {
            if (!Objects.equals(gameHolder.getGame(), game)) {
                continue;
            }
            if (game.getPlayerTeam(player.getUniqueId()) == null) { // spectator
                return;
            }
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
            // check if all players in the game are in the party and the party leader is the one who is terminating the game
            if (partyPlayerPair != null && game.players().allMatch(uuidTeamEntry -> partyPlayerPair.getA().hasUUID(uuidTeamEntry.getKey()))) {
                if (partyPlayerPair.getA().getPartyLeader().getUUID().equals(player.getUniqueId())) {
                    endGameInstance(player, gameHolder, game);
                    player.sendMessage(Component.text("Game has been terminated. Warping back to lobby...", NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("You are not the party leader, unable to terminate game.", NamedTextColor.RED));
                }
            } else {
                if (game.warlordsPlayers().count() > 1) {
                    player.sendMessage(Component.text("You are not the only player in the game, unable to terminate game.", NamedTextColor.RED));
                } else {
                    endGameInstance(player, gameHolder, game);
                }
            }

            return;
        }
    }

    private static void endGameInstance(Player player, GameManager.GameHolder holder, Game game) {
        if (holder.getGame() == null) {
            return;
        }
        if (holder.getGame().isFrozen()) {
            holder.getGame().clearFrozenCauses();
        }
        State state = game.getState();
        if (state instanceof PreLobbyState || state instanceof PlayingState) {
            game.setNextState(new EndState(game, null));
            player.sendMessage(Component.text("Game has been terminated. Warping back to lobby...", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("The game is not in endable state, instead it is in " + state.getClass().getSimpleName(), NamedTextColor.RED));
        }
    }

}


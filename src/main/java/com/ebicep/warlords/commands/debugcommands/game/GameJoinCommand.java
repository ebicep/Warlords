package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Objects;

@CommandAlias("gamejoin")
@CommandPermission("group.administrator")
public class GameJoinCommand extends BaseCommand {

    @Default
    @Description("Joins your current game if spectator")
    public void joinGame(@Conditions("requireGame") Player player, @Default("BLUE") Team team) {
        Game playerGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (GameManager.GameHolder gameHolder : Warlords.getGameManager().getGames()) {
            Game game = gameHolder.getGame();
            if (Objects.equals(game, playerGame)) {
                if (game.getPlayers().get(player.getUniqueId()) != null) {
                    ChatChannels.sendDebugMessage(player, Component.text("You are already in this game!", NamedTextColor.RED));
                    return;
                }

                WarlordsPlayer warlordsPlayer = game
                        .getCachedPlayers()
                        .stream()
                        .filter(wp -> wp.getUuid().equals(player.getUniqueId()))
                        .findFirst()
                        .orElse(new WarlordsPlayer(
                                player,
                                game,
                                team
                        ));
                Warlords.addPlayer(warlordsPlayer);
                game.addPlayer(player, false);
                game.setPlayerTeam(player, team);
                LobbyLocationMarker location = LobbyLocationMarker.getFirstLobbyLocation(game, team);
                if (location != null) {
                    player.teleport(location.getLocation());
                } else {
                    warlordsPlayer.respawn();
                }
                break;
            }
        }
    }

}

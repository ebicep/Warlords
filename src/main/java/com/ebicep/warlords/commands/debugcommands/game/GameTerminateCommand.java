package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@CommandAlias("terminategame|endgame")
@CommandPermission("warlords.game.end")
public class GameTerminateCommand extends BaseCommand {

    public static void terminateGameMatching(CommandIssuer issuer, Predicate<GameHolder> gamePredicate, String from) {
        List<GameHolder> inactiveGames = new ArrayList<>();
        List<GameHolder> otherStateGames = new ArrayList<>();
        for (GameHolder gameHolder : Warlords.getGameManager().getGames()) {
            Game game = gameHolder.getGame();
            if (game == null) {
                inactiveGames.add(gameHolder);
                continue;
            }
            if (game.isFrozen()) {
                game.clearFrozenCauses();
            }
            Optional<PlayingState> state = game.getState(PlayingState.class);
            if (!state.isPresent()) {
                otherStateGames.add(gameHolder);
                continue;
            }
            if (gamePredicate.test(gameHolder)) {
                game.setNextState(new EndState(game, null));
                ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Killed game from " + from + ": " + gameHolder.getName() + " | " + gameHolder.getMap().getMapName() + " | " + game.playersCount() + " player" + (game.playersCount() == 1 ? "" : "s"), true);
            }
        }
        ChatChannels.sendDebugMessage(
                issuer,
                ChatColor.RED + "(" + inactiveGames.size() + ") Skipped Inactive terminate game from " + from + ": " +
                        inactiveGames.stream()
                                .map(GameHolder::getName)
                                .collect(Collectors.joining(", ")),
                true);
        ChatChannels.sendDebugMessage(
                issuer,
                ChatColor.RED + "(" + otherStateGames.size() + ") Skipped Other State terminate game from " + from + ": " +
                        otherStateGames.stream()
                                .map(gameHolder -> gameHolder.getName() + "(" + gameHolder.getGame().getState().getClass().getSimpleName() + ")")
                                .collect(Collectors.joining(", ")),
                true);
    }

    @Default
    @Description("Terminates your current game")
    public void terminateGame(@Conditions("requireGame") Player player) {
        Game playerGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (GameHolder gameHolder : Warlords.getGameManager().getGames()) {
            Game game = gameHolder.getGame();
            if (Objects.equals(game, playerGame)) {
                game.setNextState(new EndState(game, null));
                ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Terminated own game " + gameHolder.getName(), true);
                break;
            }
        }
    }

    @Subcommand("all")
    @CommandPermission("warlords.game.end.remote")
    @Description("Terminates all games")
    public void terminateAllGames(CommandIssuer issuer) {
        terminateGameMatching(issuer, gameHolder -> true, "ALL");
    }

    @Subcommand("map")
    @CommandPermission("warlords.game.end.remote")
    @Description("Terminates all games matching map")
    public void terminateGameFromMap(CommandIssuer issuer, GameMap map) {
        terminateGameMatching(issuer, game -> Objects.equals(game.getGame().getMap(), map), "MAP");
    }

    @Subcommand("gamemode")
    @CommandPermission("warlords.game.end.remote")
    @Description("Terminates all games matching gamemode")
    public void terminateGameFromGameMode(CommandIssuer issuer, GameMode gameMode) {
        terminateGameMatching(issuer, game -> Objects.equals(game.getGame().getGameMode(), gameMode), "GAMEMODE");
    }

    @Subcommand("gameid")
    @CommandCompletion("@gameids")
    @CommandPermission("warlords.game.end.remote")
    @Description("Terminates all games with matching id")
    public void terminateGameFromGameId(CommandIssuer issuer, @Values("@gameids") UUID uuid) {
        terminateGameMatching(issuer, game -> Objects.equals(game.getGame().getGameId(), uuid), "GAMEID");
    }


    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }
}
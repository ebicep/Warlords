package com.ebicep.warlords.commands2.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands2.miscellaneouscommands.ChatChannelCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

@CommandAlias("killgame")
@CommandPermission("warlords.game.end.remote")
public class GameKillCommand extends BaseCommand {

    public static void killGameMatching(CommandIssuer issuer, Predicate<GameHolder> gamePredicate, String from) {
        List<String> skippedGames = new ArrayList<>();
        for (GameHolder game : Warlords.getGameManager().getGames()) {
            if (game.getGame() == null) {
                skippedGames.add(game.getName());
                continue;
            }
            if (gamePredicate.test(game)) {
                game.forceEndGame();
                ChatChannelCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Killed game from " + from + ": " + game.getName() + " - " + game.getMap() + " - " + game.getGame().playersCount() + " players");
            }
        }
        ChatChannelCommand.sendDebugMessage(issuer, ChatColor.RED + "Skipped inactive kill game from " + from + ": " + skippedGames + " - " + skippedGames.size() + " games");
    }

    @Default
    @Description("Kills your current game")
    public void killGame(@Conditions("requireGame") Player player) {
        Game playerGame = Objects.requireNonNull(Warlords.getPlayer(player)).getGame();
        for (GameHolder game : Warlords.getGameManager().getGames()) {
            if (Objects.equals(game.getGame(), playerGame)) {
                game.forceEndGame();
                ChatChannelCommand.sendDebugMessage(player, ChatColor.GREEN + "Killed own game " + game.getName());
                break;
            }
        }
    }

    @Subcommand("all")
    @Description("Kill all games")
    public void killAllGames(CommandIssuer issuer) {
        for (GameHolder game : Warlords.getGameManager().getGames()) {
            game.forceEndGame();
        }
        ChatChannelCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Killed all games");
    }

    @Subcommand("map")
    @Description("Kill all games matching map")
    public void killGameFromMap(CommandIssuer issuer, GameMap map) {
        killGameMatching(issuer, game -> Objects.equals(game.getGame().getMap(), map), "MAP");
    }

    @Subcommand("gamemode")
    @Description("Kill all games matching gamemode")
    public void killGameFromGameMode(CommandIssuer issuer, GameMode gameMode) {
        killGameMatching(issuer, game -> Objects.equals(game.getGame().getGameMode(), gameMode), "GAMEMODE");
    }

    @Subcommand("gameid")
    @CommandCompletion("@gameids")
    @Description("Kill all games with matching id")
    public void killGameFromGameId(CommandIssuer issuer, @Values("@gameids") UUID uuid) {
        killGameMatching(issuer, game -> Objects.equals(game.getGame().getGameId(), uuid), "GAMEID");
    }


    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }
}

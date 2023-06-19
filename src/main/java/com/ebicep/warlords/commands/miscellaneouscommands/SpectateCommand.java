package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.java.StringUtils.toTitleHumanCase;

@CommandAlias("spectate")
public class SpectateCommand extends BaseCommand {

    public static void openSpectateMenu(Player player) {
        List<Game> games = Warlords.getGameManager().getGames().stream()
                                   .filter(gameHolder -> gameHolder.getGame() != null && gameHolder.getGame().acceptsSpectators())
                                   .map(GameHolder::getGame)
                                   .sorted(Comparator.comparing(Game::getStartTime))
                                   .toList();
        //1-7 = 3
        //8-14 = 4
        int rows = (games.size() - 1) / 7 + 3;
        Menu menu = new Menu("Current Games", 9 * rows);

        int column = 1;
        int row = 1;
        int numberOfGames = 0;
        for (Game game : games) {
            if (game.getGameMode() == GameMode.LOBBY && !player.isOp()) {
                continue;
            }
            ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK)
                    .name(Component.text("Game - ID: " + game.getGameId(), NamedTextColor.GREEN))
                    .lore(
                            Component.text("Map: ", NamedTextColor.GRAY).append(Component.text(game.getMap().getMapName(), NamedTextColor.RED)),
                            Component.text("Gamemode: ", NamedTextColor.GRAY).append(Component.text(game.getGameMode().getName(), NamedTextColor.RED)),
                            Component.text("Addons: ", NamedTextColor.GRAY).append(Component.text(game.getAddons()
                                                                                                      .stream()
                                                                                                      .map(e -> toTitleHumanCase(e.name()))
                                                                                                      .collect(Collectors.joining(", ")), NamedTextColor.RED)),
                            Component.text("Players: ", NamedTextColor.GRAY).append(Component.text(game.warlordsPlayers().count(), NamedTextColor.RED))
                    );
            if (GameMode.isPvE(game.getGameMode())) {
                game.warlordsPlayers().forEach(warlordsPlayer -> {
                    itemBuilder.addLore(Component.text(" - ", NamedTextColor.GRAY).append(Component.text(warlordsPlayer.getName(), NamedTextColor.AQUA)));
                });
            }
            menu.setItem(column,
                    row,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (game.isClosed()) {
                            player.sendMessage(Component.text("This game is no longer running", NamedTextColor.RED));
                            openSpectateMenu(player);
                            return;
                        }
                        if (!game.acceptsSpectators()) {
                            player.sendMessage(Component.text("This game does not accepts spectators", NamedTextColor.RED));
                            openSpectateMenu(player);
                            return;
                        }
                        Optional<Game> currentGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
                        if (currentGame.isPresent() && currentGame.get().getPlayerTeam(player.getUniqueId()) != null) {
                            player.sendMessage(Component.text("You cannot use this command inside a game!", NamedTextColor.RED));
                        } else if (currentGame.isPresent() && currentGame.get().equals(game)) {
                            player.sendMessage(Component.text("You are already spectating this game", NamedTextColor.RED));
                        } else {
                            currentGame.ifPresent(value -> value.removePlayer(player.getUniqueId()));
                            game.addPlayer(player, true);
                        }
                    }
            );
            column++;
            if (column == 8) {
                column = 1;
                row++;
            }
            numberOfGames++;
        }
        if (numberOfGames == 0) {
            player.closeInventory();
            player.sendMessage(Component.text("There are no active games right now!", NamedTextColor.GREEN));
            return;
        }

        Optional<Game> currentGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
        if (currentGame.isPresent()) {
            menu.setItem(
                    4,
                    rows - 1,
                    new ItemBuilder(Material.BARRIER)
                            .name(Component.text("Return to the lobby", NamedTextColor.GREEN))
                            .get(),
                    (m, e) -> {
                        Optional<Game> currentGame1 = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
                        if (currentGame1.isPresent() && currentGame1.get().getPlayerTeam(player.getUniqueId()) != null) {
                            player.sendMessage(Component.text("You cannot use this command inside a game!", NamedTextColor.GREEN));
                        } else {
                            currentGame1.get().removePlayer(player.getUniqueId());
                        }
                    }
            );
        }

        menu.openForPlayer(player);
    }

    @Default
    @Description("Opens the spectate menu")
    public void spectate(@Conditions("outsideGame") Player player) {
        openSpectateMenu(player);
    }

    @Subcommand("gametoggle")
    @CommandPermission("group.administrator")
    @Description("Toggles spectating for current game")
    public void disableSpectating(@Conditions("requireGame") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        game.setAcceptsSpectators(!game.acceptsSpectators());
        if (game.acceptsSpectators()) {
            ChatChannels.sendDebugMessage(player, Component.text("Spectating is now enabled for this game", NamedTextColor.GREEN));
        } else {
            ChatChannels.sendDebugMessage(player, Component.text("Spectating is now disabled for this game", NamedTextColor.RED));
        }
    }

}

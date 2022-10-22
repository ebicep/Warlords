package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.warlords.Utils.toTitleHumanCase;

@CommandAlias("spectate")
public class SpectateCommand extends BaseCommand {

    public static void openSpectateMenu(Player player) {
        Menu menu = new Menu("Current Games", 9 * 3);

        int column = 1;
        int row = 1;
        for (GameHolder holder : Warlords.getGameManager().getGames()) {
            Game game = holder.getGame();
            if (game != null && game.acceptsSpectators()) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Game - ID: " + game.getGameId())
                        .lore(
                                ChatColor.GRAY + "Map: " + ChatColor.RED + game.getMap().getMapName(),
                                ChatColor.GRAY + "Gamemode: " + ChatColor.RED + game.getGameMode().getName(),
                                ChatColor.GRAY + "Addons: " + ChatColor.RED + game.getAddons()
                                        .stream()
                                        .map(e -> toTitleHumanCase(e.name()))
                                        .collect(Collectors.joining(", ")),
                                ChatColor.GRAY + "Players: " + ChatColor.RED + game.warlordsPlayers().count()
                        );
                if (game.getGameMode() == GameMode.WAVE_DEFENSE) {
                    game.warlordsPlayers().forEach(warlordsPlayer -> {
                        itemBuilder.addLore(" - " + warlordsPlayer.getName() + "\n");
                    });
                }
                menu.setItem(column,
                        row,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (game.isClosed()) {
                                player.sendMessage(ChatColor.RED + "This game is no longer running");
                                openSpectateMenu(player);
                                return;
                            }
                            if (!game.acceptsSpectators()) {
                                player.sendMessage(ChatColor.RED + "This game does not accepts spectators");
                                openSpectateMenu(player);
                                return;
                            }
                            Optional<Game> currentGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
                            if (currentGame.isPresent() && currentGame.get().getPlayerTeam(player.getUniqueId()) != null) {
                                player.sendMessage(ChatColor.RED + "You cannot use this command inside a game!");
                            } else if (currentGame.isPresent() && currentGame.get().equals(game)) {
                                player.sendMessage(ChatColor.RED + "You are already spectating this game");
                            } else {
                                currentGame.ifPresent(value -> value.removePlayer(player.getUniqueId()));
                                game.addPlayer(player, true);
                            }
                        }
                );
                column++;
                if (column == 7) {
                    column = 1;
                    row++;
                }
            }
        }


        Optional<Game> currentGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
        if (currentGame.isPresent()) {
            menu.setItem(
                    4,
                    2,
                    new ItemBuilder(Material.BARRIER)
                            .name(ChatColor.GREEN + "Return to the lobby")
                            .get(),
                    (m, e) -> {
                        Optional<Game> currentGame1 = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
                        if (currentGame1.isPresent() && currentGame1.get().getPlayerTeam(player.getUniqueId()) != null) {
                            player.sendMessage(ChatColor.RED + "You cannot use this command inside a game!");
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
        if (Warlords.getGameManager().getGames().stream().noneMatch(e -> e.getGame() != null && e.getGame().acceptsSpectators())) {
            player.sendMessage(ChatColor.RED + "There are no active games right now!");
            return;
        }
        openSpectateMenu(player);
    }
}

package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SpectateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayerOutsideGame(sender);
        if (player != null) {
            if (!Warlords.getGameManager().getGames().stream().anyMatch(e -> e.getGame() != null && e.getGame().acceptsSpectators())) {
                sender.sendMessage(ChatColor.RED + "There are no active games right now!");
                return true;
            }

            Optional<Game> currentGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
            if (currentGame.isPresent() && currentGame.get().getPlayerTeam(player.getUniqueId()) != null) {
                sender.sendMessage(ChatColor.RED + "You cannot use this command inside a game!");
                return true;
            }

            openSpectateMenu(player);

            return true;
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("spectate").setExecutor(this);
    }

    public static void openSpectateMenu(Player player) {
        Menu menu = new Menu("Current Games", 9 * 3);

        int column = 0;
        int row = 0;
        for (GameHolder holder : Warlords.getGameManager().getGames()) {
            Game game = holder.getGame();
            if (game != null && game.acceptsSpectators()) {
                menu.setItem(column,
                        row,
                        new ItemBuilder(Material.BOOK)
                                .name(ChatColor.GREEN + "Game " + game.getGameId())
                                .lore(
                                        ChatColor.DARK_GRAY + "Map - " + ChatColor.RED + game.getMap().getMapName(),
                                        ChatColor.DARK_GRAY + "GameMode - " + ChatColor.RED + game.getGameMode(),
                                        ChatColor.DARK_GRAY + "Addons - " + ChatColor.RED + game.getAddons(),
                                        ChatColor.DARK_GRAY + "Players - " + ChatColor.RED + game.playersCount()
                                )
                                .get(),
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
                                if (currentGame.isPresent()) {
                                    currentGame.get().removePlayer(player.getUniqueId());
                                }
                                game.addPlayer(player, true);
                            }
                        }
                );
                column++;
                if(column > 8) {
                    column = 0;
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
}

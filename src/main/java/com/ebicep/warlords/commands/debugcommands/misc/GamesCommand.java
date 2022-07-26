package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase.previousGames;
import static com.ebicep.warlords.util.warlords.Utils.woolSortedByColor;


public class GamesCommand implements CommandExecutor {

    public static void openGamesDebugMenu(Player player) {
        Menu menu = new Menu("Games Debug", 9 * 6);

        for (int i = 0; i < previousGames.size(); i++) {
            DatabaseGameBase game = previousGames.get(previousGames.size() - i - 1);
            menu.setItem(
                    i % 7 + 1,
                    i / 7 + 1,
                    new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + game.getDate())
                            .lore(game.getLore())
                            .get(),
                    (m, e) -> openGameEditorMenu(player, game)
            );
        }

        menu.setItem(2, 5,
                new ItemBuilder(Material.WORKBENCH)
                        .name(ChatColor.GREEN + "Set Hologram Visibility")
                        .get(),
                (m, e) -> Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility)
        );
        menu.setItem(3, 5,
                new ItemBuilder(Material.WORKBENCH)
                        .name(ChatColor.GREEN + "Reload Holograms")
                        .get(),
                (m, e) -> Bukkit.dispatchCommand(player, "games reload")
        );
        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openGameEditorMenu(Player player, DatabaseGameBase game) {
        Menu menu = new Menu(game.getDate(), 9 * 5);

        menu.setItem(4, 0,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + game.getDate())
                        .lore(game.getLore())
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(1, 2,
                new ItemBuilder(Material.WATER_BUCKET)
                        .name(ChatColor.GREEN + "Add Game")
                        .get(),
                (m, e) -> {
                    Menu.openConfirmationMenu(player,
                            "Confirm Add Game",
                            3,
                            Collections.singletonList(ChatColor.GRAY + "Add Game"),
                            Collections.singletonList(ChatColor.GRAY + "Go back"),
                            (m2, e2) -> {
                                player.sendMessage(ChatColor.GREEN + "Adding Game: " + ChatColor.YELLOW + game.getDate());
                                DatabaseGameBase.addGameToDatabase(game, player);
                                openGameEditorMenu(player, game);
                            },
                            (m2, e2) -> openGameEditorMenu(player, game),
                            (m2) -> {
                            }
                    );

                }
        );
        menu.setItem(2, 2,
                new ItemBuilder(Material.LAVA_BUCKET)
                        .name(ChatColor.GREEN + "Remove Game")
                        .get(),
                (m, e) -> {
                    Menu.openConfirmationMenu(player,
                            "Confirm Remove Game",
                            3,
                            Collections.singletonList(ChatColor.GRAY + "Remove Game"),
                            Collections.singletonList(ChatColor.GRAY + "Go back"),
                            (m2, e2) -> {
                                player.sendMessage(ChatColor.GREEN + "Removing Game: " + ChatColor.YELLOW + game.getDate());
                                DatabaseGameBase.removeGameFromDatabase(game, player);
                                openGameEditorMenu(player, game);
                            },
                            (m2, e2) -> openGameEditorMenu(player, game),
                            (m2) -> {
                            }
                    );

                }
        );
        menu.setItem(3, 2,
                new ItemBuilder(Material.ANVIL)
                        .name(ChatColor.GREEN + "Edit Addons")
                        .get(),
                (m, e) -> openGameAddonEditorMenu(player, game, new ArrayList<>(game.getGameAddons()))
        );
        if (game instanceof DatabaseGameCTF) {
            menu.setItem(4, 2,
                    new ItemBuilder(Team.BLUE.item)
                            .name(ChatColor.GREEN + "Edit Blue Score")
                            .get(),
                    (m, e) -> {
                        SignGUI.open(player, new String[]{"", "0 <= X <= 1000", "Current Blue", "Score: " + ((DatabaseGameCTF) game).getBluePoints()},
                                (p, lines) -> {
                                    String score = lines[0];
                                    try {
                                        int newScore = Integer.parseInt(score);
                                        if (newScore < 0 || newScore > 1000) {
                                            p.sendMessage(ChatColor.RED + "Score must be between 0 and 1000");
                                            return;
                                        }
                                        player.sendMessage(ChatColor.GREEN + "Setting Score: " + ChatColor.YELLOW + game.getDate());
                                        p.sendMessage(ChatColor.GREEN + "Old Blue: " + ChatColor.BLUE + ((DatabaseGameCTF) game).getBluePoints());
                                        p.sendMessage(ChatColor.GREEN + "New Blue: " + ChatColor.BLUE + newScore);
                                        ((DatabaseGameCTF) game).setBluePoints(newScore);
                                    } catch (Exception e1) {
                                        p.sendMessage(ChatColor.RED + "Invalid Score");
                                    }
                                    openGameEditorMenu(player, game);
                                }
                        );
                    }
            );
            menu.setItem(5, 2,
                    new ItemBuilder(Team.RED.item)
                            .name(ChatColor.GREEN + "Edit Red Score")
                            .get(),
                    (m, e) -> {
                        SignGUI.open(player, new String[]{"", "0 <= X <= 1000", "Current Red", "Score: " + ((DatabaseGameCTF) game).getRedPoints()},
                                (p, lines) -> {
                                    String score = lines[0];
                                    try {
                                        int newScore = Integer.parseInt(score);
                                        if (newScore < 0 || newScore > 1000) {
                                            p.sendMessage(ChatColor.RED + "Score must be between 0 and 1000");
                                            return;
                                        }
                                        player.sendMessage(ChatColor.GREEN + "Setting Score: " + ChatColor.YELLOW + game.getDate());
                                        p.sendMessage(ChatColor.GREEN + "Old Red: " + ChatColor.RED + ((DatabaseGameCTF) game).getRedPoints());
                                        p.sendMessage(ChatColor.GREEN + "New Red: " + ChatColor.RED + newScore);
                                        ((DatabaseGameCTF) game).setRedPoints(newScore);
                                    } catch (Exception e1) {
                                        p.sendMessage(ChatColor.RED + "Invalid Score");
                                    }
                                    openGameEditorMenu(player, game);
                                }
                        );
                    }
            );
        }


        menu.setItem(4, 4, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openGameAddonEditorMenu(Player player, DatabaseGameBase game, List<GameAddon> addons) {
        Menu menu = new Menu(game.getDate(), 9 * 6);

        menu.setItem(4, 0,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + game.getDate())
                        .lore(game.getLore())
                        .get(),
                (m, e) -> {
                }
        );

        for (int i = 0; i < GameAddon.values().length; i++) {
            GameAddon gameAddon = GameAddon.values()[i];

            boolean isASelectedAddon = addons.contains(gameAddon);
            ItemBuilder itemBuilder = new ItemBuilder(woolSortedByColor[i + 5])
                    .name(ChatColor.GREEN + gameAddon.getName())
                    .lore(ChatColor.GOLD + WordWrap.wrapWithNewline(gameAddon.getDescription(), 150));
            if (isASelectedAddon) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
                itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
            }

            menu.setItem(i % 7 + 1, 1 + i / 7,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (isASelectedAddon) {
                            addons.remove(gameAddon);
                        } else {
                            addons.add(gameAddon);
                        }
                        openGameAddonEditorMenu(player, game, addons);
                    }
            );
        }

        menu.setItem(3, 5, Menu.MENU_BACK, (m, e) -> openGameEditorMenu(player, game));
        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.setItem(5, 5,
                new ItemBuilder(Material.WOOL, 1, (short) 5)
                        .name(ChatColor.GREEN + "Set")
                        .get(),
                (m, e) -> {
                    Menu.openConfirmationMenu(player,
                            "Confirm Set Addons",
                            3,
                            addons.stream().map(gameAddon -> ChatColor.GOLD + gameAddon.getName()).collect(Collectors.toList()),
                            Collections.singletonList(ChatColor.GRAY + "Go back"),
                            (m2, e2) -> {
                                player.sendMessage(ChatColor.GREEN + "Setting Addons: " + ChatColor.YELLOW + game.getDate());
                                player.sendMessage(ChatColor.GREEN + "Old Addons: " + ChatColor.GOLD + game.getGameAddons().stream().map(GameAddon::getName).collect(Collectors.joining(", ")));
                                player.sendMessage(ChatColor.GREEN + "New Addons: " + ChatColor.GOLD + addons.stream().map(GameAddon::getName).collect(Collectors.joining(", ")));
                                game.setGameAddons(addons);
                                DatabaseManager.updateGameAsync(game);
                                openGameEditorMenu(player, game);
                            },
                            (m2, e2) -> openGameEditorMenu(player, game),
                            (m2) -> {
                            }
                    );
                }
        );

        menu.openForPlayer(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.lookupgame")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        if (args.length == 0) {
            if (sender instanceof Player) {
                openGamesDebugMenu((Player) sender);
            }
        } else {
            switch (args[0].toLowerCase()) {
                case "reload":
                    sender.sendMessage(ChatColor.GREEN + "Deleting Holograms");
                    previousGames.forEach(DatabaseGameBase::deleteHolograms);
                    sender.sendMessage(ChatColor.GREEN + "Creating Holograms");
                    previousGames.forEach(DatabaseGameBase::createHolograms);
                    sender.sendMessage(ChatColor.GREEN + "Setting Visibility");
                    Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility);
                    return true;
                case "list":
                    StringBuilder stringBuilder = new StringBuilder(ChatColor.GREEN + "Previous Games - \n");
                    for (int i = 0; i < previousGames.size(); i++) {
                        stringBuilder.append(ChatColor.YELLOW).append(i).append(". ").append(previousGames.get(i).getGameLabel()).append("\n");
                    }
                    sender.sendMessage(stringBuilder.toString());
                    return true;
                case "edit":
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.RED + "Usage: /games edit <game date>");
                        return true;
                    }

                    String date = StringUtils.join(args, " ", 1, args.length);
                    sender.sendMessage(ChatColor.GREEN + "Locating game with date " + date);

                    Warlords.newChain()
                            .asyncFirst(() -> DatabaseManager.gameService.findByDate(date))
                            .syncLast(databaseGameBase -> {
                                if (databaseGameBase == null) {
                                    sender.sendMessage(ChatColor.RED + "Game not found.");
                                } else if (sender instanceof Player) {
                                    openGameEditorMenu((Player) sender, databaseGameBase);
                                }
                            }).execute();
                    return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /games [add/remove] <game number>");
                return true;
            }

            if (!NumberUtils.isNumber(args[1])) {
                sender.sendMessage(ChatColor.RED + "Invalid game number!");
                return true;
            }

            int gameNumber = Integer.parseInt(args[1]);
            if (gameNumber >= previousGames.size() || gameNumber < 0) {
                sender.sendMessage(ChatColor.RED + "Invalid game number!");
                return true;
            }

            String input = args[0];
            switch (input.toLowerCase()) {
                case "add":
                    sender.sendMessage(ChatColor.GREEN + "Adding game!");
                    DatabaseGameBase.addGameToDatabase(previousGames.get(gameNumber), sender instanceof Player ? (Player) sender : null);
                    return true;
                case "remove":
                    sender.sendMessage(ChatColor.RED + "Removing game!");
                    DatabaseGameBase.removeGameFromDatabase(previousGames.get(gameNumber), sender instanceof Player ? (Player) sender : null);
                    return true;
            }
        }


        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("games").setExecutor(this);
    }

}

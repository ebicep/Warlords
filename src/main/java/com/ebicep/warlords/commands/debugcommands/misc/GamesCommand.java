package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.warlords.Utils;
import io.github.rapha149.signgui.SignGUI;
import io.github.rapha149.signgui.SignGUIAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase.previousGames;
import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;


@CommandAlias("games")
@CommandPermission("warlords.game.lookupgame")
public class GamesCommand extends BaseCommand {

    public static final Set<String> PLAYER_NAMES = new HashSet<>();

    public static void openGamesDebugMenu(Player player) {
        Menu menu = new Menu("Games Debug", 9 * 6);

        for (int i = 0; i < previousGames.size(); i++) {
            DatabaseGameBase game = previousGames.get(previousGames.size() - i - 1);
            menu.setItem(
                    i % 7 + 1,
                    i / 7 + 1,
                    new ItemBuilder(Material.BOOK)
                            .name(Component.text(game.getDate(), NamedTextColor.GREEN))
                            .lore(game.getLore())
                            .get(),
                    (m, e) -> openGameEditorMenu(player, game)
            );
        }

        menu.setItem(2, 5,
                new ItemBuilder(Material.CRAFTING_TABLE)
                        .name(Component.text("Set Hologram Visibility", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility)
        );
        menu.setItem(3, 5,
                new ItemBuilder(Material.CRAFTING_TABLE)
                        .name(Component.text("Reload Holograms", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> player.performCommand("games reload")
        );
        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openGameEditorMenu(Player player, DatabaseGameBase game) {
        Menu menu = new Menu(game.getDate(), 9 * 5);

        menu.setItem(4, 0,
                new ItemBuilder(Material.BOOK)
                        .name(Component.text(game.getDate(), NamedTextColor.GREEN))
                        .lore(game.getLore())
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(1, 2,
                new ItemBuilder(Material.WATER_BUCKET)
                        .name(Component.text("Add Game", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    Menu.openConfirmationMenu(player,
                            "Confirm Add Game",
                            3,
                            Collections.singletonList(Component.text("Add Game", NamedTextColor.GRAY)),
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                player.sendMessage(Component.text("Adding Game: ", NamedTextColor.GREEN).append(Component.text(game.getDate(), NamedTextColor.YELLOW)));
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
                        .name(Component.text("Remove Game", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    Menu.openConfirmationMenu(player,
                            "Confirm Remove Game",
                            3,
                            Collections.singletonList(Component.text("Remove Game", NamedTextColor.GRAY)),
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                player.sendMessage(Component.text("Removing Game: ", NamedTextColor.GREEN)
                                                            .append(Component.text(game.getDate(), NamedTextColor.YELLOW)));
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
                        .name(Component.text("Edit Addons", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> openGameAddonEditorMenu(player, game, new ArrayList<>(game.getGameAddons()))
        );
        if (game instanceof DatabaseGameCTF) {
            menu.setItem(4, 2,
                    new ItemBuilder(Team.BLUE.woolItem)
                            .name(Component.text("Edit Blue Score", NamedTextColor.GREEN))
                            .get(),
                    (m, e) -> {
                        SignGUI.builder()
                               .setLines("", "0 <= X <= 1000", "Current Blue", "Score: " + ((DatabaseGameCTF) game).getBluePoints())
                               .setHandler((p, lines) -> {
                                   String score = lines.getLine(0);
                                   try {
                                       int newScore = Integer.parseInt(score);
                                       if (newScore < 0 || newScore > 1000) {
                                           p.sendMessage(Component.text("Score must be between 0 and 1000", NamedTextColor.RED));
                                           return Collections.singletonList(SignGUIAction.displayNewLines(lines.getLines()));
                                       }
                                       player.sendMessage(Component.text("Setting Score: ", NamedTextColor.GREEN)
                                                                   .append(Component.text(game.getDate(), NamedTextColor.YELLOW)));
                                       p.sendMessage(Component.text("Old Blue: ", NamedTextColor.GREEN)
                                                              .append(Component.text(((DatabaseGameCTF) game).getBluePoints(), NamedTextColor.BLUE)));
                                        p.sendMessage(Component.text("New Blue: ", NamedTextColor.GREEN)
                                                               .append(Component.text(newScore, NamedTextColor.BLUE)));
                                        ((DatabaseGameCTF) game).setBluePoints(newScore);
                                   } catch (Exception e1) {
                                       p.sendMessage(Component.text("Invalid Score", NamedTextColor.GREEN));
                                   }
                                   new BukkitRunnable() {
                                       @Override
                                       public void run() {
                                           openGameEditorMenu(player, game);
                                       }
                                   }.runTaskLater(Warlords.getInstance(), 1);
                                   return null;
                               }).build().open(player);
                    }
            );
            menu.setItem(5, 2,
                    new ItemBuilder(Team.RED.woolItem)
                            .name(Component.text("Edit Red Score", NamedTextColor.GREEN))
                            .get(),
                    (m, e) -> {
                        SignGUI.builder()
                               .setLines("", "0 <= X <= 1000", "Current Red", "Score: " + ((DatabaseGameCTF) game).getRedPoints())
                               .setHandler((p, lines) -> {
                                   String score = lines.getLine(0);
                                   try {
                                       int newScore = Integer.parseInt(score);
                                       if (newScore < 0 || newScore > 1000) {
                                           p.sendMessage(Component.text("Score must be between 0 and 1000", NamedTextColor.RED));
                                           return null;
                                       }
                                       player.sendMessage(Component.text("Setting Score: ", NamedTextColor.GREEN)
                                                                   .append(Component.text(game.getDate(), NamedTextColor.YELLOW)));
                                       p.sendMessage(Component.text("Old Red: ", NamedTextColor.GREEN)
                                                              .append(Component.text(((DatabaseGameCTF) game).getRedPoints(), NamedTextColor.RED)));
                                        p.sendMessage(Component.text("New Red: ", NamedTextColor.GREEN)
                                                               .append(Component.text(newScore, NamedTextColor.RED)));
                                        ((DatabaseGameCTF) game).setRedPoints(newScore);
                                   } catch (Exception e1) {
                                       p.sendMessage(Component.text("Invalid Score", NamedTextColor.RED));
                                   }
                                   new BukkitRunnable() {
                                       @Override
                                       public void run() {
                                           openGameEditorMenu(player, game);
                                       }
                                   }.runTaskLater(Warlords.getInstance(), 1);
                                   return null;
                               }).build().open(player);
                    }
            );
        }


        menu.setItem(4, 4, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openGameAddonEditorMenu(Player player, DatabaseGameBase<?> game, List<GameAddon> addons) {
        Menu menu = new Menu(game.getDate(), 9 * 6);

        menu.setItem(4, 0,
                new ItemBuilder(Material.BOOK)
                        .name(Component.text(game.getDate(), NamedTextColor.GREEN))
                        .lore(game.getLore())
                        .get(),
                (m, e) -> {
                }
        );

        for (int i = 0; i < GameAddon.VALUES.length; i++) {
            GameAddon gameAddon = GameAddon.VALUES[i];

            boolean isASelectedAddon = addons.contains(gameAddon);
            ItemBuilder itemBuilder = new ItemBuilder(Utils.getWoolFromIndex(i + 5))
                    .name(Component.text(gameAddon.getName(), NamedTextColor.GREEN))
                    .lore(WordWrap.wrap(Component.text(gameAddon.getDescription(), NamedTextColor.GOLD), 150));
            if (isASelectedAddon) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
                ;
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
                new ItemBuilder(Material.LIME_WOOL)
                        .name(Component.text("Set", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    Menu.openConfirmationMenu(player,
                            "Confirm Set Addons",
                            3,
                            addons.stream().map(gameAddon -> Component.text(gameAddon.getName(), NamedTextColor.GOLD)).collect(Collectors.toList()),
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                player.sendMessage(Component.text("Setting Addons: ", NamedTextColor.GREEN)
                                                            .append(Component.text(game.getDate(), NamedTextColor.YELLOW)));
                                player.sendMessage(Component.text("Old Addons: ", NamedTextColor.GREEN)
                                                            .append(Component.text(game.getGameAddons()
                                                                                       .stream()
                                                                                       .map(GameAddon::getName)
                                                                                       .collect(Collectors.joining(", ")), NamedTextColor.YELLOW)));
                                player.sendMessage(Component.text("New Addons: ", NamedTextColor.GREEN)
                                                            .append(Component.text(addons.stream()
                                                                                         .map(GameAddon::getName)
                                                                                         .collect(Collectors.joining(", ")), NamedTextColor.YELLOW)));
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

    @Default
    public void games(Player player) {
        openGamesDebugMenu(player);
    }

    @Subcommand("reload")
    @Description("Reloads game holograms")
    public void reload(CommandIssuer issuer) {
        sendDebugMessage(issuer, Component.text("Reloading Game Holograms", NamedTextColor.GREEN));
        previousGames.forEach(DatabaseGameBase::deleteHolograms);
        previousGames.forEach(DatabaseGameBase::createHolograms);
        Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility);
    }

    @Subcommand("list")
    @Description("Prints list of games")
    public void list(CommandIssuer issuer) {
        TextComponent.Builder list = Component.empty().color(NamedTextColor.YELLOW)
                                              .append(Component.text("Previous Games - ", NamedTextColor.GREEN))
                                              .append(Component.newline())
                                              .toBuilder();
        for (int i = 0; i < previousGames.size(); i++) {
            list.append(Component.text(i + ". " + previousGames.get(i).getGameLabel()))
                .append(Component.newline());
        }
        sendDebugMessage(issuer, list.build());
    }

    @Subcommand("edit")
    @Conditions("database:game")
    @Description("Opens game editor from date")
    public void edit(Player player, String date) {
        sendDebugMessage(player, Component.text("Locating game with date " + date, NamedTextColor.GREEN));

        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.gameService.findByDate(date))
                .syncLast(databaseGameBase -> {
                    if (databaseGameBase == null) {
                        sendDebugMessage(player, Component.text("Game not found", NamedTextColor.RED));
                    } else {
                        sendDebugMessage(player, Component.text("Game found", NamedTextColor.GREEN));
                        openGameEditorMenu(player, databaseGameBase);
                    }

                }).execute();
    }

    @Subcommand("add")
    @Conditions("database:game")
    @Description("Adds game to database")
    public void add(CommandIssuer issuer, @Conditions("limits:previousGames") Integer gameNumber) {
        DatabaseGameBase databaseGame = previousGames.get(gameNumber);
        sendDebugMessage(issuer, Component.text("Adding game " + databaseGame.getDate(), NamedTextColor.GREEN));
        DatabaseGameBase.addGameToDatabase(databaseGame, issuer.isPlayer() ? issuer.getIssuer() : null);
    }

    @Subcommand("remove")
    @Conditions("database:game")
    @Description("Removes game from database")
    public void remove(CommandIssuer issuer, @Conditions("limits:previousGames") Integer gameNumber) {
        DatabaseGameBase databaseGame = previousGames.get(gameNumber);
        sendDebugMessage(issuer, Component.text("Removing game " + databaseGame.getDate(), NamedTextColor.GREEN));
        DatabaseGameBase.removeGameFromDatabase(databaseGame, issuer.isPlayer() ? issuer.getIssuer() : null);
    }

    @Subcommand("getnames")
    public void getNames(CommandIssuer issuer) {
        for (String playerName : PLAYER_NAMES) {
            ChatChannels.sendDebugMessage(issuer, Component.text(playerName, NamedTextColor.AQUA));
        }
    }

    @Subcommand("setcounted")
    public void setCounted(CommandIssuer issuer, @Conditions("limits:previousGames") Integer gameNumber, boolean counted) {
        DatabaseGameBase databaseGame = previousGames.get(gameNumber);
        sendDebugMessage(issuer, Component.text("Setting game " + databaseGame.getDate() + " to counted: " + counted, NamedTextColor.GREEN));
        databaseGame.setCounted(counted);
        DatabaseManager.updateGameAsync(databaseGame);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }
}

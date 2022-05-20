package com.ebicep.warlords.menu.debugmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.state.TimerDebugAble;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.util.warlords.Utils.woolSortedByColor;

public class DebugMenuGameOptions {

    public static void openGameMenu(Player player) {
        Menu menu = new Menu("Game Options", 9 * 4);
        ItemStack[] itemStack = {
                new ItemBuilder(Material.DARK_OAK_DOOR_ITEM)
                        .name(ChatColor.GREEN + "Start")
                        .get(),
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Games")
                        .get(),
        };
        for (int i = 0; i < itemStack.length; i++) {
            int index = i + 1;
            menu.setItem(index, 1, itemStack[i],
                    (m, e) -> {
                        switch (index) {
                            case 1:
                                StartMenu.openMapPickerMenu(player);
                                break;
                            case 2:
                                GamesMenu.openGameSelectorMenu(player);
                                break;
                        }
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (m, e) -> DebugMenu.openDebugMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static class StartMenu {

        public static void openMapPickerMenu(Player player) {
            Menu menu = new Menu("Map Picker", 9 * 6);
            GameMap[] values = GameMap.values();
            for (int i = 0; i < values.length; i++) {
                GameMap map = values[i];
                String mapName = map.getMapName();
                menu.setItem(i % 7 + 1, 1 + i / 7,
                        new ItemBuilder(woolSortedByColor[i + 5])
                                .name(ChatColor.GREEN + mapName)
                                .lore(ChatColor.GRAY + "Available Gamemodes: " + ChatColor.GOLD + map.getGameModes().stream().map(gameMode -> gameMode.name).collect(Collectors.joining(", ")))
                                .get(),
                        (m, e) -> openMapGameModesMenu(player, map)
                );
            }
            if (player.hasPermission("warlords.game.customtoggle")) {
                menu.setItem(3, 5, MENU_BACK, (m, e) -> openGameMenu(player));
            }
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openMapGameModesMenu(Player player, GameMap selectedGameMap) {
            int menuHeight = (4 + selectedGameMap.getGameModes().size() / 7);
            Menu menu = new Menu(selectedGameMap.getMapName(), 9 * menuHeight);

            for (int i = 0; i < selectedGameMap.getGameModes().size(); i++) {
                GameMode gameMode = selectedGameMap.getGameModes().get(i);
                menu.setItem(i % 7 + 1, 1 + i / 7,
                        new ItemBuilder(woolSortedByColor[i + 5])
                                .name(ChatColor.GREEN + gameMode.name)
                                .get(),
                        (m, e) -> {
                            List<GameAddon> addons = new ArrayList<>();
                            addons.add(GameAddon.PRIVATE_GAME);
                            if (!player.isOp()) {
                                addons.add(GameAddon.CUSTOM_GAME);
                            }
                            openMapsAddonsMenu(player, selectedGameMap, gameMode, addons);
                        }
                );
            }

            menu.setItem(3, menuHeight - 1, MENU_BACK, (m, e) -> openMapPickerMenu(player));
            menu.setItem(4, menuHeight - 1, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openMapsAddonsMenu(Player player, GameMap selectedGameMap, GameMode selectedGameMode, List<GameAddon> addons) {
            int menuHeight = (4 + GameAddon.values().length / 7);
            Menu menu = new Menu(selectedGameMap.getMapName() + " - " + selectedGameMode.name, 9 * menuHeight);

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
                                if (!player.hasPermission("warlords.game.customtoggle") && gameAddon.equals(GameAddon.CUSTOM_GAME)) {
                                    player.sendMessage(ChatColor.RED + "Only players with the Game Starter rank or higher can modify this addon!");
                                } else if (gameAddon.equals(GameAddon.PRIVATE_GAME) && !player.isOp()) {
                                    player.sendMessage(ChatColor.RED + "Games started from the start menu are automatically private!");
                                } else {
                                    addons.remove(gameAddon);
                                }
                            } else {
                                if (!player.hasPermission("warlords.game.freezetoggle") && gameAddon.equals(GameAddon.FREEZE_GAME)) {
                                    player.sendMessage(ChatColor.RED + "Only players with the Game Starter rank or higher can modify this addon!");
                                } else {
                                    addons.add(gameAddon);
                                }
                            }
                            openMapsAddonsMenu(player, selectedGameMap, selectedGameMode, addons);
                        }
                );
            }
            if (player.hasPermission("warlords.game.customtoggle")) {
                menu.setItem(4,
                        0,
                        new ItemBuilder(Material.DIAMOND_BLOCK)
                                .name(ChatColor.GREEN + "Comps Preset")
                                .lore(ChatColor.GOLD + "Select this to use the comps preset.\n- Private Game\n- Freeze Failsafe")
                                .get(),
                        (m, e) -> {
                            List<GameAddon> gameAddons = new ArrayList<>();
                            gameAddons.add(GameAddon.PRIVATE_GAME);
                            gameAddons.add(GameAddon.FREEZE_GAME);
                            StringBuilder stringAddons = new StringBuilder();
                            gameAddons.forEach(gameAddon -> {
                                stringAddons.append("addon:").append(gameAddon.name()).append(" ");
                            });
                            System.out.println(player.getName() + " - map:" + selectedGameMap.getMapName() + " category:" + selectedGameMode.name() + " " + stringAddons);
                            GameStartCommand.startGame(player, ("map:" + selectedGameMap.name() + " category:" + selectedGameMode.name() + " " + stringAddons).split(" "));
                        });
            }
            menu.setItem(3, menuHeight - 1, MENU_BACK, (m, e) -> openMapGameModesMenu(player, selectedGameMap));
            menu.setItem(4, menuHeight - 1, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.setItem(5, menuHeight - 1, new ItemBuilder(Material.WOOL, 1, (short) 5).name(ChatColor.GREEN + "Start").get(), (m, e) -> {
                StringBuilder stringAddons = new StringBuilder();
                if (addons.isEmpty()) {
                    stringAddons.append("addon:NULL");
                } else {
                    addons.forEach(gameAddon -> {
                        stringAddons.append("addon:").append(gameAddon.name()).append(" ");
                    });
                }
                System.out.println(player.getName() + " - map:" + selectedGameMap.getMapName() + " category:" + selectedGameMode.name() + " " + stringAddons);
                GameStartCommand.startGame(player, ("map:" + selectedGameMap.name() + " category:" + selectedGameMode.name() + " " + stringAddons).split(" "));
                //Bukkit.getServer().dispatchCommand(player, "start map:" + selectedGameMap.name() + " category:" + selectedGameMode.name() + " " + stringAddons);
            });
            menu.openForPlayer(player);
        }
    }

    static class GamesMenu {

        public static void openGameSelectorMenu(Player player) {
            Menu menu = new Menu("Game Selector", 9 * 5);
            List<Game> games = Warlords.getGameManager().getGames().stream()
                    .map(GameManager.GameHolder::getGame)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            int x = 1;
            int y = 1;
            for (Game game : games) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Game - " + game.getGameId())
                        .lore(ChatColor.DARK_GRAY + "Map - " + ChatColor.RED + game.getMap().getMapName(),
                                ChatColor.DARK_GRAY + "GameMode - " + ChatColor.RED + game.getGameMode(),
                                ChatColor.DARK_GRAY + "Addons - " + ChatColor.RED + game.getAddons(),
                                ChatColor.DARK_GRAY + "Players - " + ChatColor.RED + game.playersCount());
                if (Warlords.getPlayer(player) != null && Warlords.getPlayer(player).getGame() == game) {
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                }
                menu.setItem(
                        x,
                        y,
                        itemBuilder.get(),
                        (m, e) -> openGameEditorMenu(player, game));
                x++;
                if (x == 7) {
                    x = 1;
                    y++;
                }
            }

            menu.setItem(3, 4, MENU_BACK, (m, e) -> openGameMenu(player));
            menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openGameEditorMenu(Player player, Game game) {
            Menu menu = new Menu("Game Editor", 9 * 5);

            //GAME
            menu.setItem(1, 1,
                    new ItemBuilder(Material.DIODE)
                            .name(ChatColor.GREEN + "Timer")
                            .get(),
                    (m, e) -> openTimerMenu(player, game));
            menu.setItem(2, 1,
                    new ItemBuilder(Material.SIGN)
                            .name(ChatColor.GREEN + "Edit Team Scores")
                            .get(),
                    (m, e) -> openTeamScoreEditorMenu(player, game));
            menu.setItem(3, 1,
                    new ItemBuilder(Material.ICE)
                            .name(ChatColor.GREEN + "Freeze Game")
                            .get(),
                    (m, e) -> {
                        if (game.isFrozen()) {
                            game.removeFrozenCause("Debug");
                        } else {
                            game.addFrozenCause("Debug");
                        }
                        System.out.println("[DEBUG] " + player.getName() + " froze game " + game.getGameId());
                    });

            //PLAYER
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            if (warlordsPlayer != null && warlordsPlayer.getGame() == game) {
                menu.setItem(1, 2,
                        new ItemBuilder(Warlords.getHead(player))
                                .name(ChatColor.GREEN + "Player Options")
                                .get(),
                        (m, e) -> DebugMenuPlayerOptions.openPlayerMenu(player, Warlords.getPlayer(player))
                );
            }

            menu.setItem(2, 2,
                    new ItemBuilder(Material.NOTE_BLOCK)
                            .name(ChatColor.GREEN + "Team Options")
                            .get(),
                    (m, e) -> {
                        DebugMenuTeamOptions.openTeamMenu(player, game);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().getName().equals("Team Options")) {
                                    DebugMenuTeamOptions.openTeamMenu(player, game);
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Warlords.getInstance(), 20, 20);
                    }
            );

            menu.setItem(3, 4, MENU_BACK, (m, e) -> openGameSelectorMenu(player));
            menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openTimerMenu(Player player, Game game) {
            TimerDebugAble timerDebugAble = (TimerDebugAble) game.getState();

            Menu menu = new Menu("Timer", 9 * 4);
            /*menu.setItem(2, 1,
                    new ItemBuilder(Material.WOOD_BUTTON)
                            .name(ChatColor.GREEN + "Reset")
                            .get(),
                    (m, e) -> {
                        timerDebugAble.resetTimer();
                        player.sendMessage(ChatColor.RED + "DEV: " + ChatColor.GREEN + "Timer reset");
                        System.out.println("[DEBUG] " + player.getName() + " reset timer of game " + game.getGameId());
                    }
            );*/
            menu.setItem(3, 1,
                    new ItemBuilder(Material.STONE_BUTTON)
                            .name(ChatColor.GREEN + "Skip")
                            .get(),
                    (m, e) -> {
                        timerDebugAble.skipTimer();
                        player.sendMessage(ChatColor.RED + "DEV: " + ChatColor.GREEN + "Timer skipped");
                        System.out.println("[DEBUG] " + player.getName() + " skip timer of game " + game.getGameId());
                    }
            );
            menu.setItem(5, 1,
                    new ItemBuilder(Material.WATCH)
                            .name(ChatColor.GREEN + "Set")
                            .get(),
                    (m, e) -> {
                        for (Option option : game.getOptions()) {
                            if (option instanceof WinAfterTimeoutOption) {
                                SignGUI.open(player, new String[]{"", "^^^^^^^", "Enter new Time Left", "XX:XX"}, (p, lines) -> {
                                    String time = lines[0];
                                    try {
                                        if (!time.contains(":")) {
                                            throw new Exception();
                                        }
                                        int minutes = Integer.parseInt(time.split(":")[0]);
                                        int seconds = Integer.parseInt(time.split(":")[1]);
                                        if (minutes < 0 || seconds < 0) {
                                            throw new Exception();
                                        }
                                        ((WinAfterTimeoutOption) option).setTimeRemaining(minutes * 60 + seconds);
                                        player.sendMessage(ChatColor.RED + "DEV: " + ChatColor.GREEN + "Timer set to " + time);
                                        System.out.println("[DEBUG] " + player.getName() + " set timer of game " + game.getGameId() + " to " + time);
                                    } catch (Exception exception) {
                                        p.sendMessage(ChatColor.RED + "Invalid time");
                                    }
                                    openTimerMenu(player, game);
                                });
                                break;
                            }
                        }
                    }
            );
            menu.setItem(3, 3, MENU_BACK, (m, e) -> openGameEditorMenu(player, game));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openTeamScoreEditorMenu(Player player, Game game) {
            Menu menu = new Menu("Select Team", 9 * 4);
            int x = 1;
            for (Team team : TeamMarker.getTeams(game)) {
                menu.setItem(
                        x,
                        1,
                        new ItemBuilder(team.item)
                                .name(team.teamColor + team.name)
                                .get(),
                        (m, e) -> {
                            SignGUI.open(player, new String[]{"", "^^^^^^^", "Enter new score", "Team: " + team.getName()}, (p, lines) -> {
                                String line = lines[0];
                                try {
                                    int score = Integer.parseInt(line);
                                    if (score < 0) {
                                        throw new NumberFormatException();
                                    }
                                    game.setPoints(team, score);
                                    player.sendMessage(ChatColor.RED + "DEV: " + ChatColor.GREEN + "Score of team " + team.getName() + " set to " + score);
                                    System.out.println("[DEBUG] " + player.getName() + " set score of team " + team.getName() + " to " + score);
                                } catch (NumberFormatException exception) {
                                    p.sendMessage(ChatColor.RED + "Invalid score");
                                }
                                openTeamScoreEditorMenu(player, game);
                            });
                        }
                );
                x++;
            }

            menu.setItem(3, 3, MENU_BACK, (m, e) -> openGameEditorMenu(player, game));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

    }

}

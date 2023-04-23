package com.ebicep.warlords.menu.debugmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.state.TimerDebugAble;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.warlords.Utils;
import de.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;


public class DebugMenuGameOptions {

    public static void openGameMenu(Player player) {
        Menu menu = new Menu("Game Options", 9 * 4);
        ItemStack[] itemStack = {
                new ItemBuilder(Material.DARK_OAK_DOOR)
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
                            case 1 -> StartMenu.openGamemodeMenu(player);
                            case 2 -> GamesMenu.openGameSelectorMenu(player);
                        }
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (m, e) -> DebugMenu.openDebugMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static class StartMenu {

        public static void openGamemodeMenu(Player player) {
            GameMode[] values = GameMode.VALUES;
            int rows = values.length / 7 + 4;
            Menu menu = new Menu("Gamemode Picker", 9 * rows);
            int i = -1;
            for (GameMode gm : values) {
                if (gm.isHiddenInMenu()) {
                    continue;
                }
                i++;
                menu.setItem(i % 7 + 1, i / 7 + 1,
                        new ItemBuilder(Material.BLACK_WOOL)
                                .name(ChatColor.GOLD + ChatColor.BOLD.toString() + gm.getName())
                                .get(),
                        (m, e) -> openMapMenu(player, gm)
                );
            }

            if (player.hasPermission("warlords.game.customtoggle")) {
                menu.setItem(3, rows - 1, MENU_BACK, (m, e) -> openGameMenu(player));
            }

            menu.setItem(4, rows - 1, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openMapMenu(Player player, GameMode gm) {
            Menu menu = new Menu(gm.getName(), 9 * 5);
            GameMap[] values = GameMap.VALUES;
            int i = -1;
            for (GameMap map : values) {
                if (!map.getGameModes().contains(gm)) {
                    continue;
                }
                i++;
                menu.setItem(i % 7 + 1, 1 + i / 7,
                        new ItemBuilder(Utils.getWoolFromIndex(i + 5))
                                .name(ChatColor.GREEN + map.getMapName())
                                .get(),
                        (m, e) -> {
                            EnumSet<GameAddon> addons = EnumSet.noneOf(GameAddon.class);
                            addons.add(GameAddon.PRIVATE_GAME);
                            if (!player.hasPermission("warlords.game.customtoggle")) {
                                addons.add(GameAddon.CUSTOM_GAME);
                            }
                            openMapsAddonsMenu(player, map, gm, addons);
                        }
                );
            }

            menu.setItem(3, 4, MENU_BACK, (m, e) -> openGamemodeMenu(player));
            menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openMapsAddonsMenu(Player player, GameMap selectedGameMap, GameMode selectedGameMode, EnumSet<GameAddon> addons) {
            int menuHeight = (4 + GameAddon.VALUES.length / 7);
            Menu menu = new Menu(selectedGameMap.getMapName() + " - " + selectedGameMode.getName(), 9 * menuHeight);

            for (int i = 0; i < GameAddon.VALUES.length; i++) {
                GameAddon gameAddon = GameAddon.VALUES[i];

                if (!player.isOp() && gameAddon == GameAddon.TOURNAMENT_MODE) {
                    continue;
                }

                boolean isASelectedAddon = addons.contains(gameAddon);
                ItemBuilder itemBuilder = new ItemBuilder(Utils.getWoolFromIndex(i + 5))
                        .name(ChatColor.GREEN + gameAddon.getName())
                        .loreLEGACY(ChatColor.GOLD + WordWrap.wrapWithNewline(gameAddon.getDescription(), 150));
                if (isASelectedAddon) {
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                }

                menu.setItem(i % 7 + 1, 1 + i / 7,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (isASelectedAddon) {
                                boolean customToggle = !player.hasPermission("warlords.game.customtoggle");
                                if (customToggle && gameAddon.equals(GameAddon.CUSTOM_GAME)) {
                                    player.sendMessage(ChatColor.RED + "Only players with the Game Starter rank or higher can modify this addon!");
                                } else if (customToggle && gameAddon.equals(GameAddon.PRIVATE_GAME)) {
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
                                .loreLEGACY(ChatColor.GOLD + "Select this to use the comps preset.\n- Private Game\n- Freeze Failsafe")
                                .get(),
                        (m, e) -> GameStartCommand.startGameFromDebugMenu(player, false, queueEntryBuilder -> {
                            queueEntryBuilder
                                    .setMap(selectedGameMap)
                                    .setGameMode(selectedGameMode)
                                    .setRequestedGameAddons(GameAddon.PRIVATE_GAME, GameAddon.FREEZE_GAME);
                        })
                );
                menu.setItem(3, menuHeight - 1, MENU_BACK, (m, e) -> openMapMenu(player, selectedGameMode));
                menu.setItem(4, menuHeight - 1, MENU_CLOSE, ACTION_CLOSE_MENU);
                menu.setItem(5, menuHeight - 1, new ItemBuilder(Material.LIME_WOOL).name(ChatColor.GREEN + "Start").get(), (m, e) -> {
                    //safe guard
                    if (!player.isOp()) {
                        addons.remove(GameAddon.TOURNAMENT_MODE);
                    }
                    GameStartCommand.startGameFromDebugMenu(player,
                            addons.contains(GameAddon.TOURNAMENT_MODE) && e.isShiftClick(),
                            queueEntryBuilder -> {
                                queueEntryBuilder
                                        .setMap(selectedGameMap)
                                        .setGameMode(selectedGameMode)
                                        .setRequestedGameAddons(addons);
                            }
                    );
                });
                menu.openForPlayer(player);
            }
        }
    }

    public static class GamesMenu {

        public static void openGameSelectorMenu(Player player) {
            List<Game> games = Warlords.getGameManager().getGames().stream()
                                       .filter(gameHolder -> gameHolder.getGame() != null && gameHolder.getGame().acceptsSpectators())
                                       .map(GameManager.GameHolder::getGame)
                                       .toList();

            int rows = (games.size() - 1) / 7 + 4;
            Menu menu = new Menu("Game Selector", 9 * rows);

            int x = 1;
            int y = 1;
            for (Game game : games) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Game - " + game.getGameId())
                        .loreLEGACY(ChatColor.DARK_GRAY + "Map - " + ChatColor.RED + game.getMap().getMapName(),
                                ChatColor.DARK_GRAY + "GameMode - " + ChatColor.RED + game.getGameMode(),
                                ChatColor.DARK_GRAY + "Addons - " + ChatColor.RED + game.getAddons(),
                                ChatColor.DARK_GRAY + "Players - " + ChatColor.RED + game.playersCount()
                        );
                if (game.hasPlayer(player.getUniqueId())) {
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                }
                menu.setItem(
                        x,
                        y,
                        itemBuilder.get(),
                        (m, e) -> openGameEditorMenu(player, game)
                );
                x++;
                if (x == 8) {
                    x = 1;
                    y++;
                }
            }

            menu.setItem(3, rows - 1, MENU_BACK, (m, e) -> openGameMenu(player));
            menu.setItem(4, rows - 1, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openGameEditorMenu(Player player, Game game) {
            Menu menu = new Menu("Game Editor", 9 * 5);
            menu.setItem(1, 1,
                    new ItemBuilder(Material.REPEATER)
                            .name(ChatColor.GREEN + "Timer")
                            .get(),
                    (m, e) -> openTimerMenu(player, game)
            );
            menu.setItem(2, 1,
                    new ItemBuilder(Material.OAK_SIGN)
                            .name(ChatColor.GREEN + "Edit Team Scores")
                            .get(),
                    (m, e) -> openTeamScoreEditorMenu(player, game)
            );
            menu.setItem(3, 1,
                    new ItemBuilder(Material.ICE)
                            .name(ChatColor.GREEN + "Freeze Game")
                            .get(),
                    (m, e) -> {
                        if (game.isFrozen()) {
                            game.removeFrozenCause(Component.text("Debug"));
                        } else {
                            game.addFrozenCause(Component.text("Debug"));
                        }
                        sendDebugMessage(player, player.getName() + " froze game " + game.getGameId());
                    }
            );
            WarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
            if (warlordsPlayer != null && warlordsPlayer.getGame() == game) {
                menu.setItem(1, 2,
                        new ItemBuilder(HeadUtils.getHead(player))
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
                        DebugMenuTeamOptions.openTeamSelectorMenu(player, game);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (PlainTextComponentSerializer.plainText().serialize(player.getOpenInventory().title()).equals("Team Options")) {
                                    DebugMenuTeamOptions.openTeamSelectorMenu(player, game);
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
            menu.setItem(3, 1,
                    new ItemBuilder(Material.STONE_BUTTON)
                            .name(ChatColor.GREEN + "Skip")
                            .get(),
                    (m, e) -> {
                        timerDebugAble.skipTimer();
                        sendDebugMessage(player, ChatColor.GREEN + "Skip timer of game " + game.getGameId());
                    }
            );
            menu.setItem(5, 1,
                    new ItemBuilder(Material.CLOCK)
                            .name(ChatColor.GREEN + "Set")
                            .get(),
                    (m, e) -> {
                        for (Option option : game.getOptions()) {
                            if (option instanceof WinAfterTimeoutOption) {
                                new SignGUI()
                                        .lines("", "^^^^^^^", "Enter new Time Left", "XX:XX")
                                        .onFinish((p, lines) -> {
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
                                                sendDebugMessage(player, ChatColor.GREEN + "Set timer of game " + game.getGameId() + " to " + time);
                                            } catch (Exception exception) {
                                                p.sendMessage(ChatColor.RED + "Invalid time");
                                            }
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    openTimerMenu(player, game);
                                                }
                                            }.runTaskLater(Warlords.getInstance(), 1);
                                            return null;
                                        }).open(player);
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
                            new SignGUI()
                                    .lines("", "^^^^^^^", "Enter new score", "Team: " + team.getName())
                                    .onFinish((p, lines) -> {
                                        String line = lines[0];
                                        try {
                                            int score = Integer.parseInt(line);
                                            if (score < 0) {
                                                throw new NumberFormatException();
                                            }
                                            game.setPoints(team, score);
                                            sendDebugMessage(player, ChatColor.GREEN + "Set score of team " + team.getName() + " to " + score);
                                        } catch (NumberFormatException exception) {
                                            p.sendMessage(ChatColor.RED + "Invalid score");
                                        }
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                openTeamScoreEditorMenu(player, game);
                                            }
                                        }.runTaskLater(Warlords.getInstance(), 1);
                                        return null;
                                    }).open(player);
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


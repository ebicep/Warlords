package com.ebicep.warlords.pve;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.*;

public class DifficultyMenu {

    public static void openPveMenu(Player player) {
        Menu menu = new Menu("Pve Menu", 9 * 4);
        menu.setItem(
                3,
                1,
                new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.GREEN + "Start a private PvE game").get(),
                (m, e) -> openDifficultyMenu(player, true)
        );
        menu.setItem(
                5,
                1,
                new ItemBuilder(Material.REDSTONE_COMPARATOR).name(ChatColor.GREEN + "Join a public PvE game").get(),
                (m, e) -> openDifficultyMenu(player, false)
        );
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openDifficultyMenu(Player player, boolean privateGame) {
        Menu menu = new Menu("Difficulty Menu", 9 * 4);
        DifficultyIndex[] index = DifficultyIndex.NON_EVENT;
        for (int i = 0; i < index.length; i++) {
            DifficultyIndex difficulty = index[i];
            int finalI = i;
            menu.setItem(
                    9 / 2 - index.length + 1 + i * 2,
                    1,
                    new ItemBuilder(Material.REDSTONE_LAMP_OFF)
                            .name(difficulty.getDifficultyColor() + ChatColor.BOLD.toString() + difficulty.getName())
                            .lore(ChatColor.GRAY + difficulty.getDescription())
                            .get(),
                    (m, e) -> {
                        GameMap map = null;
                        switch (finalI) {
                            case 0:
                                map = GameMap.ILLUSION_APERTURE;
                                break;
                            case 1:
                                map = GameMap.ILLUSION_RIFT;
                                break;
                            case 2:
                                map = GameMap.ILLUSION_VALLEY;
                                break;
                            case 3:
                                map = GameMap.ILLUSION_CROSSFIRE;
                                break;
                        }
                        GameMap finalMap = map;
                        if (finalMap != null) {
                            if (privateGame) {
                                GameStartCommand.startGamePvE(player, queueEntryBuilder ->
                                        queueEntryBuilder.setMap(finalMap)
                                                .setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                                );
                            } else {
                                GameStartCommand.startGamePvE(player, queueEntryBuilder ->
                                        queueEntryBuilder.setMap(finalMap)

                                );
                            }
                        }
                    }
            );
            menu.setItem(3, 3, MENU_BACK, (m, e) -> openPveMenu(player));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        }
        menu.openForPlayer(player);
    }
}

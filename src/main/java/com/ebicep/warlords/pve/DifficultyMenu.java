package com.ebicep.warlords.pve;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class DifficultyMenu {

    public static void openDifficultyMenu(Player player) {
        Menu menu = new Menu("Difficulty Menu", 9 * 4);
        DifficultyIndex[] index = DifficultyIndex.NON_EVENT;
        for (int i = 0; i < index.length; i++) {
            DifficultyIndex difficulty = index[i];
            int finalI = i;
            menu.setItem(
                    9 / 2 - index.length + 1 + i * 2,
                    1,
                    new ItemBuilder(Material.REDSTONE_LAMP)
                            .name(Component.text(difficulty.getName(), difficulty.getDifficultyColor(), TextDecoration.BOLD))
                            .lore(difficulty.getDescription())
                            .get(),
                    (m, e) -> {
                        GameMap map;
                        map = switch (finalI) {
                            case 0 -> GameMap.ILLUSION_APERTURE;
                            case 1 -> GameMap.ILLUSION_RIFT;
                            case 2 -> GameMap.ILLUSION_VALLEY;
                            case 3 -> GameMap.ILLUSION_VALLEY2;
                            case 4 -> GameMap.ILLUSION_CROSSFIRE;
                            default -> null;
                        };
                        GameMap finalMap = map;
                        if (finalMap != null) {
                            GameStartCommand.startGamePvE(player, GameMode.WAVE_DEFENSE, queueEntryBuilder ->
                                    queueEntryBuilder.setMap(finalMap)
                                                     .setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                            );
                        }
                    }
            );
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        }
        menu.openForPlayer(player);
    }
}

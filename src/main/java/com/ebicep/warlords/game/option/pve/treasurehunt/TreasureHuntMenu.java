package com.ebicep.warlords.game.option.pve.treasurehunt;

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

public class TreasureHuntMenu {

    public static void openDifficultyMenu(Player player) {
        Menu menu = new Menu("Cryptic Conquest Menu", 9 * 4);
        TreasureHuntIndex[] index = TreasureHuntIndex.values();
        for (int i = 0; i < index.length; i++) {
            TreasureHuntIndex hunt = index[i];
            int finalI = i;
            menu.setItem(
                    9 / 2 - index.length + 1 + i * 2,
                    1,
                    new ItemBuilder(Material.REDSTONE_LAMP)
                            .name(Component.text(hunt.getName(), hunt.getHuntColor(), TextDecoration.BOLD))
                            .lore(hunt.getDescription())
                            .get(),
                    (m, e) -> {
                        GameMap map;
                        map = switch (finalI) {
                            case 0 -> GameMap.TREASURE_HUNT;
                            default -> null;
                        };
                        GameMap finalMap = map;
                        if (finalMap != null) {
                            GameStartCommand.startGamePvE(player, GameMode.TREASURE_HUNT, queueEntryBuilder ->
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

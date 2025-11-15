package com.ebicep.warlords.game.option.pve.treasurehunt;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.*;

public class TreasureHuntMenu {

    public static void openDifficultyMenu(Player player) {
        Menu menu = new Menu("Cryptic Conquest Menu", 9 * 6);
        TreasureHuntIndex[] index = TreasureHuntIndex.values();
        for (int i = 0; i < index.length; i++) {
            TreasureHuntIndex hunt = index[i];
            int finalI = i;
            menu.setItem(4,
                    0,
                    new ItemBuilder(Material.CRYING_OBSIDIAN)
                            .name(Component.text("Conditions", NamedTextColor.LIGHT_PURPLE))
                            .addLore(WordWrap.wrapWithNewline(Component.text("To traverse the hidden hallways safely, you may only have one specialization per player." +
                                    " More will result in void instability, reducing your healing and damage done by 90%.", NamedTextColor.GRAY), 150))
                    .get(),
                    ACTION_DO_NOTHING);
            menu.setItem(
                    9 / 2 - index.length + 1 + i * 2,
                    3,
                    new ItemBuilder(Material.REDSTONE_LAMP)
                            .name(Component.text(hunt.getName(), hunt.getHuntColor(), TextDecoration.BOLD))
                            .lore(hunt.getDescription())
                            .get(),
                    (m, e) -> {
                        GameMap map;
                        map = switch (finalI) {
                            case 0 -> GameMap.DUAL_DESCENT;
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
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        }
        menu.openForPlayer(player);
    }
}

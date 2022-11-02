package com.ebicep.warlords.game.option.raid;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.*;

public class RaidMenu {

    public static void openRaidMenu(Player player) {
        Menu menu = new Menu("Raid Menu", 9 * 4);
        Raid[] index = Raid.VALUES;
        for (int i = 0; i < index.length; i++) {
            Raid raid = index[i];
            int finalI = i;
            menu.setItem(
                    9 / 2 - index.length + i,
                    1,
                    new ItemBuilder(Material.REDSTONE)
                            .name(ChatColor.RED + raid.getName())
                            .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + raid.getDescription(), 130) +
                                    "\n\nMinimum level: §c" + raid.getMinimumClassLevel())
                            .get(),
                    (m, e) -> {
                        GameMap map = null;
                        switch (finalI) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                player.sendMessage(ChatColor.RED + "WIP");
                                break;
                        }

                        /*GameMap finalMap = map;
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
                        }*/
                    }
            );
            menu.setItem(3, 3, MENU_BACK, (m, e) -> openRaidMenu(player));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        }
        menu.openForPlayer(player);
    }
}

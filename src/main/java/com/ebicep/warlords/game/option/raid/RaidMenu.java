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
                    i % 7 + 1,
                    1,
                    new ItemBuilder(Material.REDSTONE)
                            .name(ChatColor.RED + ChatColor.BOLD.toString() + raid.getName())
                            .lore(WordWrap.wrapWithNewline(ChatColor.DARK_GRAY + raid.getDescription(), 150) +
                                    "\n\n§7Minimum level: §c" + raid.getMinimumClassLevel() +
                                    "\n§7Minimum player requirement: §64" +
                                    "\n\n§7Completion Rewards:" +
                                    "\n§8+§3300.000 Class Experience" +
                                    "\n§8+§e300.000 Coins" +
                                    "\n§8+§f1.000 Synthetic Shards" +
                                    "\n§8+§61 Raid Insignia" +
                                    "\n§8+§c1 Ascendant Fragment" +
                                    "\n\n§7Possible Bonus Rewards:" +
                                    "\n§8+§650 Legend Fragments" +
                                    "\n§8+§61 Legendary Weapon"
                            )
                            .get(),
                    (m, e) -> {
                        GameMap map = null;
                        switch (finalI) {
                            case 0:
                                map = GameMap.THE_OBSIDIAN_TRAIL_RAID;
                                break;
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                player.sendMessage(ChatColor.RED + "WIP");
                                break;
                        }

                        GameMap finalMap = map;
                        if (finalMap != null) {
                            GameStartCommand.startGamePvERaid(player, queueEntryBuilder ->
                                    queueEntryBuilder.setMap(finalMap)
                                            .setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                            );
                        }
                    }
            );
            menu.setItem(3, 3, MENU_BACK, (m, e) -> openRaidMenu(player));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        }
        menu.openForPlayer(player);
    }
}

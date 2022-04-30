package com.ebicep.warlords.pve;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.*;

public class DifficultyMenu {

    public static void openPveMenu(Player player) {
        Menu menu = new Menu("Pve Menu", 9 * 4);
        menu.setItem(
                3,
                1,
                new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.GREEN + "Start a private PvE game").get(),
                (m, e) -> openDifficultyMenu(player)
        );
        menu.setItem(
                5,
                1,
                new ItemBuilder(Material.REDSTONE_COMPARATOR).name(ChatColor.GREEN + "Join a public PvE game").get(),
                (m, e) -> openDifficultyMenu(player)
        );
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openDifficultyMenu(Player player) {
        Menu menu = new Menu("Difficulty Menu", 9 * 4);
        DifficultyIndex[] index = DifficultyIndex.values();
        for (int i = 0; i < index.length; i++) {
            DifficultyIndex difficulty = index[i];

            List<String> lore = new ArrayList<>();
            lore.add("§7Difficulty Grade: " + difficulty.getDifficultyColor() + difficulty.getName());
            lore.add("");
            // Placeholder
            lore.add("§7Possible rewards:");
            lore.add("§8Coin Pouch §7(§62000§7)");
            lore.add("§9Rare Weapon");
            lore.add("§9Rare Keystone");
            lore.add("§dEpic Armor Piece");
            lore.add("§cMythical Keystone");
            lore.add("§cMythical Weapon");

            menu.setItem(
                    9 / 2 - index.length / 2 + i * 2,
                    1,
                    new ItemBuilder(Material.REDSTONE_LAMP_OFF).name(difficulty.getDifficultyColor() + difficulty.getName()).lore(lore).get(),
                    Menu.ACTION_DO_NOTHING
            );
            menu.setItem(4, 3, MENU_BACK, (m, e) -> openPveMenu(player));
            menu.setItem(3, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        }

        menu.openForPlayer(player);
    }
}

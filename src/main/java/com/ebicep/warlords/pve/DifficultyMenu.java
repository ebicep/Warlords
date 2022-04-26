package com.ebicep.warlords.pve;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DifficultyMenu {

    public static void openDifficultyMenu(Player player) {
        Menu menu = new Menu("Difficulty Menu", 9 * 6);
        DifficultyIndex[] index = DifficultyIndex.values();
        for (int i = 0; i < index.length; i++) {
            DifficultyIndex difficulty = index[i];

            List<String> lore = new ArrayList<>();
            lore.add("§7Difficulty Grade: " + difficulty.getDifficultyColor() + difficulty.getName());
            lore.add("§7Required Level: §6" + difficulty.getRequiredLevel());
            lore.add("");
            // Placeholder
            lore.add("§7Possible rewards:");
            lore.add("§8Coin Pouch §7(§62000§7) §7(§670%§7)");
            lore.add("§9Rare Weapon §7(§620%§7)");
            lore.add("§9Rare Keystone §7(§620%§7)");
            lore.add("§dEpic Armor Piece §7(§62%§7)");
            lore.add("§cMythical Keystone §7(§60.35%§7)");
            lore.add("§cMythical Weapon §7(§60.02%§7)");

            menu.setItem(
                    9 / 2 - index.length / 2 + i * 2 - 1,
                    1,
                    new ItemBuilder(Material.APPLE).name(difficulty.getDifficultyColor() + difficulty.getName()).lore(lore).get(),
                    Menu.ACTION_DO_NOTHING
            );
        }

        menu.openForPlayer(player);
    }
}

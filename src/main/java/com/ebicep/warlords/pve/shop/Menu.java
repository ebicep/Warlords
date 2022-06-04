package com.ebicep.warlords.pve.shop;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Menu {

    public static void openPveShopMenu(Player player) {
        com.ebicep.warlords.menu.Menu menu = new com.ebicep.warlords.menu.Menu("PvE Shop", 9 * 6);
        menu.setItem(4, 1, new ItemBuilder(Material.ACACIA_DOOR_ITEM)
                .name(ChatColor.GREEN + "Keystones")
                .get(),
                (m, e) -> openKeystoneMenu(player));
        menu.openForPlayer(player);
    }

    public static void openKeystoneMenu(Player player) {
        com.ebicep.warlords.menu.Menu menu = new com.ebicep.warlords.menu.Menu("Keystones", 9 * 6);
        KeystoneIndex[] index = KeystoneIndex.values();
        for (int i = 0; i < index.length; i++) {
            KeystoneIndex keystone = index[i];
            menu.setItem(
                    9 / 2 - index.length / 2 + i,
                    1,
                    new ItemBuilder(Material.REDSTONE_LAMP_OFF)
                            .name(ChatColor.GOLD + keystone.getName())
                            .lore(ChatColor.GRAY + keystone.getDescription())
                            .get(),
                    com.ebicep.warlords.menu.Menu.ACTION_DO_NOTHING
            );
        }
        menu.openForPlayer(player);
    }
}

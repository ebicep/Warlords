package com.ebicep.warlords.pve.coinshop;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CoinshopMenu {

    public static void openPveShopMenu(Player player) {
        Menu menu = new Menu("PvE Shop", 9 * 6);
        menu.setItem(9, 1, new ItemBuilder(Material.ACACIA_DOOR_ITEM)
                .name("Keystones")
                .get(),
                (m, e) -> openKeystoneMenu(player));
        menu.openForPlayer(player);
    }

    public static void openKeystoneMenu(Player player) {
        Menu menu = new Menu("Keystones", 9 * 6);
        KeystoneIndex[] index = KeystoneIndex.values();
        for (int i = 0; i < index.length; i++) {
            KeystoneIndex keystone = index[i];
            menu.setItem(
                    9 / 2 - index.length / 2 + i,
                    1,
                    new ItemBuilder(Material.REDSTONE_LAMP_OFF)
                            .name(ChatColor.GREEN + keystone.getName())
                            .get(),
                    Menu.ACTION_DO_NOTHING
            );
        }
        menu.openForPlayer(player);
    }
}

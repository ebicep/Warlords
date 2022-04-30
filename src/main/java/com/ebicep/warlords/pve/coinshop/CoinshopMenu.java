package com.ebicep.warlords.pve.coinshop;

import com.ebicep.warlords.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CoinshopMenu {

    public static void openPveShopMenu(Player player) {
        Menu menu = new Menu("PvE Shop", 9 * 6);
        menu.setItem(9, 1, new ItemStack(Material.ACACIA_DOOR), Menu.ACTION_DO_NOTHING);
        menu.openForPlayer(player);
    }

    public static void openKeystoneMenu(Player player) {
        Menu menu = new Menu("Keystones", 9 * 6);
    }
}

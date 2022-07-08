package com.ebicep.warlords.pve.events;

import com.ebicep.warlords.menu.Menu;
import org.bukkit.entity.Player;

public class MasterworksFair {

    public static void openMasterworksFairMenu(Player player) {
        Menu menu = new Menu("Masterworks Fair", 9 * 5);

        menu.setItem(4, 4, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}

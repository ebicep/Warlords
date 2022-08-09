package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.menu.Menu;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class GuildBankMenu {

    public static void openGuildBankMenu(Player player, Guild guild) {
        Menu menu = new Menu("Guild Bank", 9 * 5);


        menu.setItem(4, 4, MENU_BACK, (m, e) -> GuildMenu.openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }

}

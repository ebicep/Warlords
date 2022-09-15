package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class GuildBankMenu {

    public static void openGuildBankMenu(Player player, Guild guild) {
        Menu menu = new Menu("Guild Bank", 9 * 4);

        menu.setItem(1, 1,
                new ItemBuilder(Material.GOLD_BARDING)
                        .name(ChatColor.GREEN + "Temporary Upgrades")
                        .get(),
                (m, e) -> {
                    GuildUpgradeMenu.openGuildUpgradeTypeMenu(player, guild, GuildUpgradesTemporary.VALUES);

                }
        );
        menu.setItem(2, 1,
                new ItemBuilder(Material.DIAMOND_BARDING)
                        .name(ChatColor.GREEN + "Permanent Upgrades")
                        .get(),
                (m, e) -> {
                    GuildUpgradeMenu.openGuildUpgradeTypeMenu(player, guild, GuildUpgradesPermanent.VALUES);
                }
        );

        menu.setItem(4, 3, MENU_BACK, (m, e) -> GuildMenu.openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }

    /*
    public static void openGuildCoinConversionMenu(Player player, Guild guild) {
        Menu menu = new Menu("Guild Coin Conversion", )
    }

     */

}

package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class GuildMenu {

    public static void openGuildMenu(Guild guild, Player player, int page) {
        Menu menu = new Menu(guild.getName(), 9 * 6);

        menu.setItem(2, 0,
                new ItemBuilder(Material.GOLD_BLOCK)
                        .name(ChatColor.GREEN + "Guild Bank")
                        .lore(
                                ChatColor.GRAY + "Coins: " + ChatColor.GREEN + NumberFormat.addCommas(guild.getCoins(Timing.LIFETIME)),
                                "",
                                ChatColor.GRAY + "Click to open the guild bank"
                        )
                        .get(),
                (m, e) -> GuildBankMenu.openGuildBankMenu(player, guild)
        );

        if (player.getUniqueId().equals(guild.getCurrentMaster())) {
            menu.setItem(4, 0,
                    new ItemBuilder(Material.LEVER)
                            .name(ChatColor.GREEN + "Edit Permissions")
                            .get(),
                    (m, e) -> GuildRoleMenu.openRoleSelectorMenu(guild, player));
        }

        int playerPerPage = 36;
        List<GuildPlayer> guildPlayers = guild.getPlayers();
        for (int i = 0; i < playerPerPage; i++) {
            int index = ((page - 1) * playerPerPage) + i;
            if (index < guildPlayers.size()) {
                GuildPlayer guildPlayer = guildPlayers.get(index);
                menu.setItem(i % 9, i / 9 + 1,
                        new ItemBuilder(HeadUtils.getHead(guildPlayer.getUUID())) //TODO check if this lags
                                .name(ChatColor.GREEN + guildPlayer.getName())
                                .lore(ChatColor.GRAY + "Role: " + ChatColor.AQUA + guild.getRoleOfPlayer(guildPlayer.getUUID()).getRoleName())
                                .get(),
                        (m, e) -> {

                        });
            } else {
                break;
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> openGuildMenu(guild, player, page - 1)
            );
        }
        if (guild.getPlayers().size() > (page * playerPerPage)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> openGuildMenu(guild, player, page + 1)
            );
        }

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}

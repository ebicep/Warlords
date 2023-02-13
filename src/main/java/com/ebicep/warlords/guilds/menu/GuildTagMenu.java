package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.guilds.logs.types.oneplayer.tag.GuildLogTagBracketColor;
import com.ebicep.warlords.guilds.logs.types.oneplayer.tag.GuildLogTagNameColor;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.Colors;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Objects;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class GuildTagMenu {

    public static void openGuildTagMenu(Guild guild, Player player) {
        Menu menu = new Menu("Guild Tag: " + guild.getName(), 9 * 6);
        GuildTag guildTag = guild.getTag();


        int row = 0;
        int column = 1;
        for (Colors color : GuildTag.COLORS) {
            ItemBuilder itemBuilder = new ItemBuilder(color.wool)
                    .name(color.chatColor + "[" + guildTag.getColoredName() + color.chatColor + "]");
            if (Objects.equals(color.chatColor.toString(), guildTag.getBracketColor())) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
                itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
            }
            menu.setItem(column, row,
                    itemBuilder.get(),
                    (m, e) -> {
                        guild.log(new GuildLogTagBracketColor(player.getUniqueId(), guildTag.getBracketColor(), color.chatColor.toString()));
                        guildTag.setBracketColor(color.chatColor.toString());
                        guild.queueUpdate();
                        openGuildTagMenu(guild, player);
                    }
            );
            if (++column == 8) {
                column = 1;
                row++;
            }
        }

        menu.setItem(4, 2,
                new ItemBuilder(Material.OAK_SIGN)
                        .name(ChatColor.GREEN + "Change Tag Name")
                        .get(),
                (m, e) -> {
                    SignGUI.open(player, new String[]{"", "Enter Tag Name", "Max 6", "Characters"},
                            (p, lines) -> {
                                String newTagName = lines[0];
                                player.performCommand("guild tag " + newTagName);
                            }
                    );
                }
        );

        row = 3;
        column = 1;
        for (Colors color : GuildTag.COLORS) {
            ItemBuilder itemBuilder = new ItemBuilder(color.wool)
                    .name(guildTag.getBracketColor() + "[" + color.chatColor + guildTag.getName() + guildTag.getBracketColor() + "]");
            if (Objects.equals(color.chatColor.toString(), guildTag.getNameColor())) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
                itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
            }
            menu.setItem(column, row,
                    itemBuilder.get(),
                    (m, e) -> {
                        guild.log(new GuildLogTagNameColor(player.getUniqueId(), guildTag.getNameColor(), color.chatColor.toString()));
                        guildTag.setNameColor(color.chatColor.toString());
                        guild.queueUpdate();
                        openGuildTagMenu(guild, player);
                    }
            );
            if (++column == 8) {
                column = 1;
                row++;
            }
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> GuildMenu.openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }

}

package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.guilds.logs.types.oneplayer.tag.GuildLogTagBracketColor;
import com.ebicep.warlords.guilds.logs.types.oneplayer.tag.GuildLogTagNameColor;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.Colors;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import de.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

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
                    .name(Component.text("[", color.textColor)
                                   .append(guildTag.getColoredName())
                                   .append(Component.text("]")));
            if (Objects.equals(color.textColor.toString(), guildTag.getBracketColor())) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            menu.setItem(column, row,
                    itemBuilder.get(),
                    (m, e) -> {
                        guild.log(new GuildLogTagBracketColor(player.getUniqueId(), guildTag.getBracketColor(), color.textColor.toString()));
                        guildTag.setBracketColor(color.textColor);
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
                        .name(Component.text("Change Tag Name", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    new SignGUI()
                            .lines("", "Enter Tag Name", "Max 6", "Characters")
                            .onFinish((p, lines) -> {
                                String newTagName = lines[0];
                                player.performCommand("guild tag " + newTagName);
                                return null;
                            }).open(player);
                }
        );

        row = 3;
        column = 1;
        for (Colors color : GuildTag.COLORS) {
            ItemBuilder itemBuilder = new ItemBuilder(color.wool)
                    .name(Component.text("[", guildTag.getBracketTextColor())
                                   .append(Component.text(guildTag.getName(), color.textColor))
                                   .append(Component.text("]")));
            if (Objects.equals(color.textColor.toString(), guildTag.getNameColor())) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            menu.setItem(column, row,
                    itemBuilder.get(),
                    (m, e) -> {
                        guild.log(new GuildLogTagNameColor(player.getUniqueId(), guildTag.getNameColor(), color.textColor.toString()));
                        guildTag.setNameColor(color.textColor);
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

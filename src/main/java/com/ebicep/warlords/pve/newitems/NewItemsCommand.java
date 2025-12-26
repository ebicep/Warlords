package com.ebicep.warlords.pve.newitems;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("newitems")
@CommandPermission("group.administrator")
public class NewItemsCommand extends BaseCommand {

    @Subcommand("test")
    public void test(Player player) {
        NewItem item = new NewItem(NewItemsSetBonus.RANDOM_COMMON);
        player.getInventory().addItem(item.getItemBuilder().get());
        ChatChannels.playerSendMessage(player, ChatChannels.DEBUG,
                Component.text("Generated new item: ", NamedTextColor.GRAY)
                         .append(item.getName().hoverEvent(item.getItemBuilder().get().asHoverEvent()))
        );
    }

    @Subcommand("create")
    public void create(Player player, NewItemsSetBonus set) {
        NewItem item = new NewItem(set);
        player.getInventory().addItem(item.getItemBuilder().get());
        ChatChannels.playerSendMessage(player, ChatChannels.DEBUG,
                Component.text("Generated new item: ", NamedTextColor.GRAY)
                         .append(item.getName().hoverEvent(item.getItemBuilder().get().asHoverEvent()))
        );
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

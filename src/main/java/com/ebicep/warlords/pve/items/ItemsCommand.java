package com.ebicep.warlords.pve.items;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.items.pojos.Item;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

@CommandAlias("items")
@CommandPermission("minecraft.command.op|group.administrator")
public class ItemsCommand extends BaseCommand {


    @Subcommand("reload")
    public void reload(CommandIssuer issuer) {
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Reloading items...", true);
        Items.reload();
    }

    @Subcommand("list")
    public void list(CommandIssuer issuer) {
        Items.printAll(issuer);
    }

    @Subcommand("createall")
    public void createAll(CommandIssuer issuer) {
        Warlords.newChain()
                .async(() -> {
                    for (Items item : Items.VALUES) {
                        DatabaseManager.itemService.create(new Item(item));
                        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Created item: " + ChatColor.YELLOW + item, true);
                    }
                }).execute();
    }

}

package com.ebicep.warlords.pve.items;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.items.pojos.Item;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("items")
@CommandPermission("minecraft.command.op|group.administrator")
public class ItemsCommand extends BaseCommand {

    @Subcommand("menu")
    public void menu(Player player) {
        ItemsMenu.openItemMenu(player, 1);
    }

    @Subcommand("spawn")
    public void spawn(Player player, Items item, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            for (int i = 0; i < amount; i++) {
                databasePlayer.getPveStats().getItemsManager().addItem(item);
                ChatChannels.playerSpigotSendMessage(player, ChatChannels.DEBUG,
                        new ComponentBuilder(ChatColor.GRAY + "Spawned item: ")
                                .appendHoverItem(item.getName(), item.generateItemStack())
                );
            }
        });
    }

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


    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

package com.ebicep.warlords.pve.items;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.pve.items.menu.ItemsMenu;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.ItemTypes;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("items")
@CommandPermission("minecraft.command.op|group.administrator")
public class ItemsCommand extends BaseCommand {

    @Subcommand("menu")
    public void menu(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> ItemsMenu.openItemMenuExternal(player, false));
    }

    @Subcommand("equipmenu")
    public void equipMenu(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> ItemsMenu.openItemLoadoutMenu(player, null));
    }

    @Subcommand("generate")
    public void generate(Player player, ItemTypes type, ItemTier tier, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
        if (tier == ItemTier.ALL) {
            tier = ItemTier.ALPHA;
            ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Item tier was set to " + tier.name() + " because it was NONE", true);
        }
        ItemTier finalTier = tier;
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            for (int i = 0; i < amount; i++) {
                AbstractItem<?, ?, ?> item = type.create.apply(player.getUniqueId(), finalTier);
                databasePlayer.getPveStats().getItemsManager().addItem(item);
                ChatChannels.playerSpigotSendMessage(player, ChatChannels.DEBUG,
                        new ComponentBuilder(ChatColor.GRAY + "Spawned item: ")
                                .appendHoverItem(item.getName(), item.generateItemStack())
                );
            }
        });
    }

    @Subcommand("generaterandom")
    public void generateRandom(Player player, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            for (int i = 0; i < amount; i++) {
                ItemTypes randomItemType = ItemTypes.VALUES[random.nextInt(ItemTypes.VALUES.length)];
                ItemTier randomItemTier = ItemTier.VALID_VALUES[random.nextInt(ItemTier.VALID_VALUES.length)];
                AbstractItem<?, ?, ?> item = randomItemType.create.apply(player.getUniqueId(), randomItemTier);
                databasePlayer.getPveStats().getItemsManager().addItem(item);
                ChatChannels.playerSpigotSendMessage(player, ChatChannels.DEBUG,
                        new ComponentBuilder(ChatColor.GRAY + "Spawned item: ")
                                .appendHoverItem(item.getName(), item.generateItemStack())
                );
            }
        });
    }

    @Subcommand("clear")
    public void clear(Player player) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.getPveStats().getItemsManager().getItemInventory().clear();
            ChatChannels.playerSpigotSendMessage(player, ChatChannels.DEBUG,
                    new ComponentBuilder(ChatColor.GREEN + "Cleared items")
            );
        });
    }


//    @Subcommand("spawn")
//    public void spawn(Player player, Items item, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
//        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
//            for (int i = 0; i < amount; i++) {
//                databasePlayer.getPveStats().getItemsManager().addItem(item);
//                ChatChannels.playerSpigotSendMessage(player, ChatChannels.DEBUG,
//                        new ComponentBuilder(ChatColor.GRAY + "Spawned item: ")
//                                .appendHoverItem(item.getName(), item.generateItemStack())
//                );
//            }
//        });
//    }
//
//    @Subcommand("reload")
//    public void reload(CommandIssuer issuer) {
//        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Reloading items...", true);
//        Items.reload();
//    }
//
//    @Subcommand("list")
//    public void list(CommandIssuer issuer) {
//        Items.printAll(issuer);
//    }
//
//    @Subcommand("createall")
//    public void createAll(CommandIssuer issuer) {
//        Warlords.newChain()
//                .async(() -> {
//                    for (Items item : Items.VALUES) {
//                        DatabaseManager.itemService.create(new Item(item));
//                        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Created item: " + ChatColor.YELLOW + item, true);
//                    }
//                }).execute();
//    }


    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

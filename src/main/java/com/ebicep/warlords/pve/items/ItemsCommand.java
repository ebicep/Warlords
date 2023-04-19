package com.ebicep.warlords.pve.items;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.pve.items.menu.ItemCraftingMenu;
import com.ebicep.warlords.pve.items.menu.ItemEquipMenu;
import com.ebicep.warlords.pve.items.types.AbstractFixedItem;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AbstractSpecialItem;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.pve.items.types.fixeditems.FixedItems;
import com.ebicep.warlords.pve.items.types.specialitems.SpecialItems;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("items")
@CommandPermission("minecraft.command.op|group.administrator")
public class ItemsCommand extends BaseCommand {

    @Default
    @Subcommand("menu")
    public void menu(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> ItemEquipMenu.openItemEquipMenuExternal(player, databasePlayer));
    }

    @Subcommand("equipmenu")
    public void equipMenu(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> ItemEquipMenu.openItemLoadoutMenu(player, null, databasePlayer));
    }

    @Subcommand("forgemenu")
    public void openForgingMenu(Player player, ItemTier tier) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> ItemCraftingMenu.openItemCraftingMenu(player, databasePlayer));
    }

    @Subcommand("addfoundblessings")
    public void addFoundBlessings(Player player, @Conditions("limits:min=1,max=10") Integer amount) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.getPveStats()
                          .getItemsManager()
                          .addBlessingsFound(amount);
            ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Added " + amount + " found blessings");
                }
        );
    }

    @Subcommand("addboughtblessings")
    public void addBoughtBlessings(Player player, @Conditions("limits:min=1,max=5") Integer tier, @Conditions("limits:min=1,max=10") Integer amount) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.getPveStats()
                          .getItemsManager()
                          .getBlessingsBought()
                          .merge(tier, amount, Integer::sum);
            ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Added " + amount + " Tier " + tier + " bought blessings");
                }
        );
    }

    @Subcommand("generate")
    public void generate(Player player, ItemType type, ItemTier tier, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
        if (tier == ItemTier.NONE) {
            tier = ItemTier.ALPHA;
            ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Item tier was set to " + tier.name() + " because it was NONE");
        }
        ItemTier finalTier = tier;
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            for (int i = 0; i < amount; i++) {
                AbstractItem item = type.createBasic(finalTier);
                if (item == null) {
                    continue;
                }
                databasePlayer.getPveStats().getItemsManager().addItem(item);
                ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, Component.text(ChatColor.GRAY + "Spawned item: ").hoverEvent(item.getHoverComponent()));
            }
        });
    }

    @Subcommand("generaterandom")
    public void generateRandom(Player player, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            for (int i = 0; i < amount; i++) {
                ItemTier randomItemTier = ItemTier.VALID_VALUES[random.nextInt(ItemTier.VALID_VALUES.length)];
                AbstractItem item = ItemType.getRandom().createBasic(randomItemTier);
                if (item == null) {
                    continue;
                }
                databasePlayer.getPveStats().getItemsManager().addItem(item);
                ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, Component.text(ChatColor.GRAY + "Spawned item: ").hoverEvent(item.getHoverComponent()));
            }
        });
    }

    @Subcommand("spawnspecial")
    public void spawnSpecial(Player player, SpecialItems specialItem, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            for (int i = 0; i < amount; i++) {
                AbstractSpecialItem item = specialItem.create.get();
                databasePlayer.getPveStats().getItemsManager().addItem(item);
                ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, Component.text(ChatColor.GRAY + "Spawned item: ").hoverEvent(item.getHoverComponent()));
            }
        });
    }

    @Subcommand("spawnfixed")
    public void spawnFixed(Player player, FixedItems fixedItem) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            AbstractFixedItem item = fixedItem.create.get();
            databasePlayer.getPveStats().getItemsManager().addItem(item);
            ChatChannels.playerSendMessage(player, ChatChannels.DEBUG,
                    Component.text(ChatColor.GRAY + "Spawned item: ")
                             .hoverEvent(item.getHoverComponent())
            );
        });
    }

    @Subcommand("clear")
    public void clear(Player player) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.getPveStats().getItemsManager().getItemInventory().clear();
            ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, Component.text(ChatColor.GREEN + "Cleared items"));
        });
    }


//    @Subcommand("spawn")
//    public void spawn(Player player, Items item, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
//        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
//            for (int i = 0; i < amount; i++) {
//                databasePlayer.getPveStats().getItemsManager().addItem(item);
//                ChatChannels.playerSendMessage(player, ChatChannels.DEBUG,
//                        Component.text(ChatColor.GRAY + "Spawned item: ")
//                                .appendHoverItem(item.getName(), item.generateItemStack())
//                );
//            }
//        });
//    }
//
//    @Subcommand("reload")
//    public void reload(CommandIssuer issuer) {
//        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Reloading items...");
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
//                        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Created item: " + ChatColor.YELLOW + item);
//                    }
//                }).execute();
//    }


    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

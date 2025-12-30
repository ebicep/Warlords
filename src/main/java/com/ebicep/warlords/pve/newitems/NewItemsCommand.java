package com.ebicep.warlords.pve.newitems;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.newitems.menu.NewItemEquipMenu;
import com.ebicep.warlords.pve.newitems.menu.NewItemRerollMenu;
import com.ebicep.warlords.pve.newitems.menu.NewItemSetsMenu;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;

@CommandAlias("newitems")
@CommandPermission("group.administrator")
public class NewItemsCommand extends BaseCommand {

    @Default
    @Subcommand("menu")
    public void menu(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        NewItemEquipMenu.openItemEquipMenuExternal(player, databasePlayer);
    }

    @Subcommand("sets")
    public void sets(Player player) {
        NewItemSetsMenu.open(player);
    }

    @Subcommand("reroll")
    public void reroll(Player player) {
        NewItemRerollMenu.open(player);
    }

    @Subcommand("clear")
    public void clear(Player player, @Optional Integer count) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        NewItemsManager newItemsManager = databasePlayer.getPveStats().getNewItemsManager();
        List<NewItem> itemInventory = newItemsManager.getItemInventory();
        if (count == null) {
            itemInventory.clear();
            ChatChannels.sendDebugMessage(player, Component.text("Cleared all items from your item inventory.", NamedTextColor.GREEN));
        } else {
            for (int i = 0; i < count && !itemInventory.isEmpty(); i++) {
                itemInventory.removeLast();
            }
            ChatChannels.sendDebugMessage(player, Component.text("Cleared " + count + " items from your item inventory.", NamedTextColor.GREEN));
        }
    }

    @Subcommand("generate")
    public class GenerateItem extends BaseCommand {

        @Subcommand("random")
        public void generate(Player player, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
            for (int i = 0; i < amount; i++) {
                NewItem item = NewItemsUtils.generateRandomItem();
                addNewGeneratedItem(player, item);
            }
        }

        private static void addNewGeneratedItem(Player player, NewItem item) {
            addItem(player, item);
            ChatChannels.playerSendMessage(player, ChatChannels.DEBUG,
                    Component.text("Generated new item: ", NamedTextColor.GRAY)
                             .append(item.getName().hoverEvent(item.getItemBuilder().get().asHoverEvent()))
            );
        }

        private static void addItem(Player player, NewItem item) {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
            NewItemsManager newItemsManager = databasePlayer.getPveStats().getNewItemsManager();
            newItemsManager.addItem(item);
        }

        @Subcommand("tier")
        public void generate(Player player, NewItemTier tier, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
            for (int i = 0; i < amount; i++) {
                NewItem item = NewItemsUtils.generateRandomItem(tier);
                addNewGeneratedItem(player, item);
            }
        }

        @Subcommand("set")
        public void generate(Player player, NewItemsSetBonus setBonus, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
            for (int i = 0; i < amount; i++) {
                NewItem item = new NewItem(setBonus);
                addNewGeneratedItem(player, item);
            }
        }

    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

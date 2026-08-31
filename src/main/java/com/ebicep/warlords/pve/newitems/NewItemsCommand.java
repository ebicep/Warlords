package com.ebicep.warlords.pve.newitems;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.DatabasePlayerFuture;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.newitems.gems.Gem;
import com.ebicep.warlords.pve.newitems.gems.menu.GemMergeMenu;
import com.ebicep.warlords.pve.newitems.menu.NewItemCraftMenu;
import com.ebicep.warlords.pve.newitems.menu.NewItemEquipMenu;
import com.ebicep.warlords.pve.newitems.menu.NewItemRerollMenu;
import com.ebicep.warlords.pve.newitems.menu.NewItemSetsMenu;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;

@CommandAlias("items")
@CommandPermission("group.administrator")
public class NewItemsCommand extends BaseCommand {

    @Default
    @Subcommand("menu")
    public void menu(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        NewItemEquipMenu.openItemEquipMenuExternal(player, databasePlayer);
    }

    @Subcommand("profile")
    @CommandCompletion("@players")
    public void profile(Player player, DatabasePlayerFuture databasePlayerFuture) {
        databasePlayerFuture.future()
                .thenAccept(databasePlayer -> Bukkit.getScheduler().runTask(
                        Warlords.getInstance(),
                        () -> showLoadoutProfile(player, databasePlayer)
                ))
                .exceptionally(throwable -> {
                    Throwable cause = throwable.getCause() == null ? throwable : throwable.getCause();
                    Bukkit.getScheduler().runTask(Warlords.getInstance(), () -> player.sendMessage(
                            Component.text(
                                    cause.getMessage() == null ? "Could not load that player's item loadouts." : cause.getMessage(),
                                    NamedTextColor.RED
                            )
                    ));
                    return null;
                });
    }

    private static void showLoadoutProfile(Player player, DatabasePlayer databasePlayer) {
        NewItemsManager itemsManager = databasePlayer.getPveStats().getNewItemsManager();
        List<NewItemLoadout> loadouts = itemsManager.getLoadouts()
                .stream()
                .sorted(Comparator.comparing(NewItemLoadout::getCreationDate))
                .toList();

        player.sendMessage(Component.text("=== " + databasePlayer.getName() + "'s Item Loadouts ===", NamedTextColor.GOLD));
        if (loadouts.isEmpty()) {
            player.sendMessage(Component.text("No loadouts found.", NamedTextColor.GRAY));
            return;
        }

        for (NewItemLoadout loadout : loadouts) {
            String spec = loadout.getSpec() == null ? "Any" : loadout.getSpec().name;
            player.sendMessage(
                    Component.text(loadout.getName(), NamedTextColor.AQUA)
                            .append(Component.text(
                                    " (" + loadout.getDifficultyMode().getShortName() + " | " + spec + ")",
                                    NamedTextColor.DARK_GRAY
                            ))
            );

            List<NewItem> equippedItems = loadout.getActualItems(itemsManager)
                    .stream()
                    .sorted(Comparator.comparing(NewItem::getSlot))
                    .toList();
            if (equippedItems.isEmpty()) {
                player.sendMessage(Component.text("  No items equipped.", NamedTextColor.GRAY));
                continue;
            }

            for (NewItem item : equippedItems) {
                player.sendMessage(
                        Component.text("  " + item.getSlot().getName() + ": ", NamedTextColor.GRAY)
                                .append(item.getName().hoverEvent(
                                        item.getItemBuilder(itemsManager, loadout).get().asHoverEvent()
                                ))
                );
            }
        }
    }

    @Subcommand("sets")
    public void sets(Player player) {
        NewItemSetsMenu.open(player);
    }

    @Subcommand("reroll")
    public void reroll(Player player) {
        NewItemRerollMenu.open(player);
    }

    @Subcommand("craft")
    public void craft(Player player) {
        NewItemCraftMenu.open(player);
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

    @Subcommand("gems")
    public class Gems extends BaseCommand {

        @Subcommand("give")
        public void give(Player player, Gem gem, @Default("1") @Conditions("limits:min=1,max=1000") Integer amount) {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
            gem.addToPlayer(databasePlayer, amount);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            ChatChannels.sendDebugMessage(player, Component.text("Gave " + amount + "x ", NamedTextColor.GREEN)
                                                           .append(gem.getColoredName())
            );
        }

        @Subcommand("clear")
        public void clear(Player player) {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
            databasePlayer.getPveStats().getGems().clear();
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            ChatChannels.sendDebugMessage(player, Component.text("Cleared all of your gems.", NamedTextColor.GREEN));
        }

        @Subcommand("merge")
        public void merge(Player player) {
            GemMergeMenu.open(player);
        }

        /**
         * Unlocks sockets across the whole item inventory so the feature can be exercised without paying for it.
         */
        @Subcommand("unlockslots")
        public void unlockSlots(Player player, @Default("1") @Conditions("limits:min=1,max=3") Integer count) {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
            int unlocked = 0;
            for (NewItem item : databasePlayer.getPveStats().getNewItemsManager().getItemInventory()) {
                for (int i = 0; i < count && item.canUnlockGemSlot(); i++) {
                    item.unlockGemSlot();
                    unlocked++;
                }
            }
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            ChatChannels.sendDebugMessage(player, Component.text("Unlocked " + unlocked + " gem sockets across your item inventory.",
                    NamedTextColor.GREEN
            ));
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

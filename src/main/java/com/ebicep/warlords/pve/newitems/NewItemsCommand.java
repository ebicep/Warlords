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
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletionStage;

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

    @Subcommand("give")
    @CommandCompletion("@players * @newitempieces @range:1-10")
    @Syntax("<player> <set> <piece|all> [amount]")
    @Description("Gives a new item set, or an exact piece of it, to a player. Use all for every piece.")
    public CompletionStage<?> give(
            CommandIssuer issuer,
            DatabasePlayerFuture databasePlayerFuture,
            NewItemsSetBonus setBonus,
            String piece,
            @Default("1") @Conditions("limits:min=1,max=10") Integer amount
    ) {
        List<NewItemsSlot> configuredSlots = setBonus.getSlots();
        if (configuredSlots == null || configuredSlots.isEmpty()) {
            ChatChannels.sendDebugMessage(issuer, Component.text(setName(setBonus) + " has no configured pieces.", NamedTextColor.RED));
            return watchLookup(issuer, databasePlayerFuture);
        }

        boolean entireSet = isEntireSet(piece);
        NewItemsSlot slot = entireSet ? null : findSlot(piece);
        if (!entireSet && slot == null) {
            ChatChannels.sendDebugMessage(issuer, Component.text(
                    "Unknown piece '" + piece + "'. Use a slot name or all. Pieces: " + pieceNames(configuredSlots),
                    NamedTextColor.RED
            ));
            return watchLookup(issuer, databasePlayerFuture);
        }
        if (slot != null && !configuredSlots.contains(slot)) {
            ChatChannels.sendDebugMessage(issuer, Component.text(
                    slot.getName() + " is not part of " + setName(setBonus) + ". Pieces: " + pieceNames(configuredSlots),
                    NamedTextColor.RED
            ));
            return watchLookup(issuer, databasePlayerFuture);
        }

        List<NewItemsSlot> pieces = slot == null ? List.copyOf(configuredSlots) : List.of(slot);
        return databasePlayerFuture.future()
                .thenAccept(databasePlayer -> Bukkit.getScheduler().runTask(Warlords.getInstance(), () -> {
                    try {
                        givePieces(issuer, databasePlayer, setBonus, slot, pieces, amount);
                    } catch (RuntimeException exception) {
                        ChatChannels.sendDebugMessage(issuer, Component.text(
                                exception.getMessage() == null ? "Could not give those items." : exception.getMessage(),
                                NamedTextColor.RED
                        ));
                    }
                }))
                .exceptionally(throwable -> {
                    reportLookupFailure(issuer, throwable);
                    return null;
                });
    }

    public static List<String> completePieces(List<String> args) {
        List<String> pieces = new ArrayList<>();
        pieces.add("all");
        NewItemsSetBonus setBonus = args.size() >= 2 ? findSet(args.get(1)) : null;
        List<NewItemsSlot> slots = setBonus == null ? null : setBonus.getSlots();
        if (slots == null || slots.isEmpty()) {
            for (NewItemsSlot slot : NewItemsSlot.VALUES) {
                pieces.add(slot.name());
            }
            return pieces;
        }
        for (NewItemsSlot slot : slots) {
            pieces.add(slot.name());
        }
        return pieces;
    }

    private static CompletionStage<?> watchLookup(CommandIssuer issuer, DatabasePlayerFuture databasePlayerFuture) {
        return databasePlayerFuture.future().exceptionally(throwable -> {
            reportLookupFailure(issuer, throwable);
            return null;
        });
    }

    private static void reportLookupFailure(CommandIssuer issuer, Throwable throwable) {
        Throwable cause = throwable.getCause() == null ? throwable : throwable.getCause();
        String message = cause.getMessage() == null ? "Could not give items to that player." : cause.getMessage();
        Bukkit.getScheduler().runTask(Warlords.getInstance(), () -> ChatChannels.sendDebugMessage(
                issuer,
                Component.text(message, NamedTextColor.RED)
        ));
    }

    private static void givePieces(
            CommandIssuer issuer,
            DatabasePlayer databasePlayer,
            NewItemsSetBonus setBonus,
            NewItemsSlot slot,
            List<NewItemsSlot> pieces,
            int amount
    ) {
        NewItemsManager itemsManager = databasePlayer.getPveStats() == null ? null : databasePlayer.getPveStats().getNewItemsManager();
        if (itemsManager == null || itemsManager.getItemInventory() == null) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Could not access that player's item inventory.", NamedTextColor.RED));
            return;
        }

        List<NewItem> given = new ArrayList<>(pieces.size() * amount);
        try {
            for (int i = 0; i < amount; i++) {
                for (NewItemsSlot piece : pieces) {
                    given.add(new NewItem(setBonus, piece));
                }
            }
        } catch (RuntimeException exception) {
            ChatChannels.sendDebugMessage(issuer, Component.text(
                    "Could not create " + setName(setBonus) + " items" + (exception.getMessage() == null ? "." : ": " + exception.getMessage()),
                    NamedTextColor.RED
            ));
            return;
        }

        for (NewItem item : given) {
            itemsManager.addItem(item);
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        String playerName = databasePlayer.getName() == null ? "that player" : databasePlayer.getName();
        Component gift = giftLabel(setBonus, slot, amount);
        try {
            ChatChannels.sendDebugMessage(issuer,
                    Component.text("Gave ", NamedTextColor.GREEN)
                             .append(gift)
                             .append(Component.text(" to ", NamedTextColor.GREEN))
                             .append(Component.text(playerName, NamedTextColor.AQUA))
            );
            for (NewItem item : given) {
                ChatChannels.sendDebugMessage(issuer, Component.text(" - ", NamedTextColor.GRAY).append(item.getHoverComponent()));
            }

            Player onlineTarget = Bukkit.getPlayer(databasePlayer.getUuid());
            boolean gaveToSelf = onlineTarget != null
                    && issuer.getIssuer() instanceof Player issuerPlayer
                    && issuerPlayer.getUniqueId().equals(onlineTarget.getUniqueId());
            if (onlineTarget == null || gaveToSelf) {
                return;
            }
            onlineTarget.sendMessage(Component.text("You received ", NamedTextColor.GREEN).append(gift).append(Component.text(".", NamedTextColor.GREEN)));
        } catch (RuntimeException exception) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Gave the items to " + playerName + ".", NamedTextColor.GREEN));
        }
    }

    private static Component giftLabel(NewItemsSetBonus setBonus, NewItemsSlot slot, int amount) {
        String times = amount == 1 ? "" : amount + "x ";
        String name = slot == null ? setName(setBonus) + " set" : setName(setBonus) + " " + slot.getName();
        return Component.text(times + name, color(setBonus));
    }

    private static TextColor color(NewItemsSetBonus setBonus) {
        if (setBonus.getTier() == null || setBonus.getTier().getTextColor() == null) {
            return NamedTextColor.YELLOW;
        }
        return setBonus.getTier().getTextColor();
    }

    private static boolean isEntireSet(String piece) {
        return piece.equalsIgnoreCase("all") || piece.equalsIgnoreCase("set");
    }

    private static NewItemsSetBonus findSet(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String normalized = name.replace(' ', '_');
        for (NewItemsSetBonus setBonus : NewItemsSetBonus.VALUES) {
            if (setBonus.name().equalsIgnoreCase(normalized)) {
                return setBonus;
            }
            String displayName = setBonus.getName();
            if (displayName != null && displayName.equalsIgnoreCase(name)) {
                return setBonus;
            }
        }
        return null;
    }

    private static NewItemsSlot findSlot(String piece) {
        String normalized = piece.replace(' ', '_');
        for (NewItemsSlot slot : NewItemsSlot.VALUES) {
            if (slot.name().equalsIgnoreCase(normalized) || slot.getName().equalsIgnoreCase(piece)) {
                return slot;
            }
        }
        return null;
    }

    private static String setName(NewItemsSetBonus setBonus) {
        return setBonus.getName() == null ? setBonus.name() : setBonus.getName();
    }

    private static String pieceNames(List<NewItemsSlot> slots) {
        List<String> names = new ArrayList<>(slots.size());
        for (NewItemsSlot slot : slots) {
            names.add(slot.getName());
        }
        return String.join(", ", names);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

package com.ebicep.warlords.pve.newitems.gems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.gems.Gem;
import com.ebicep.warlords.pve.newitems.menu.NewItemEditorMenu;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewItemGemMenu {

    private static final int FIRST_SLOT_COLUMN = 2;

    public static void open(Player player, NewItem item) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu("Gem Slots", 9 * 5);

        menu.setItem(4, 0, item.getItemBuilder().get(), Menu.ACTION_DO_NOTHING);

        int maxSlots = item.getMaxGemSlots();
        for (int slot = 0; slot < maxSlots; slot++) {
            int slotIndex = slot;
            int column = FIRST_SLOT_COLUMN + slot;
            if (slot >= item.getUnlockedGemSlots()) {
                menu.setItem(column, 2, lockedSlotItem(slotIndex), (m, e) -> openUnlockConfirmation(player, databasePlayer, item, slotIndex));
                continue;
            }
            Gem socketed = item.getSocketedGem(slotIndex);
            if (socketed == null) {
                // sockets are stored densely, so a new gem always lands in the first empty one
                int firstEmpty = item.getSocketedGems().size();
                menu.setItem(column, 2, emptySlotItem(slotIndex), (m, e) -> GemSelectMenu.open(player, databasePlayer, item, firstEmpty));
            } else {
                menu.setItem(column, 2, socketedSlotItem(slotIndex, socketed), (m, e) -> {
                    if (e.getClick().isRightClick()) {
                        unsocket(player, databasePlayer, item, slotIndex, socketed);
                    } else {
                        GemSelectMenu.open(player, databasePlayer, item, slotIndex);
                    }
                });
            }
        }

        if (maxSlots == 0) {
            menu.setItem(4, 2,
                    new ItemBuilder(Material.BARRIER)
                            .name(Component.text("No Gem Slots", NamedTextColor.RED))
                            .lore(WordWrap.wrap(Component.text(
                                    item.getTier().getName() + " items cannot hold gems. Epic items and above have gem slots.",
                                    NamedTextColor.GRAY
                            ), 160))
                            .get(),
                    Menu.ACTION_DO_NOTHING
            );
        }

        menu.setItem(4, 3,
                new ItemBuilder(Material.ANVIL)
                        .name(Component.text("Merge Gems", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text("Combine duplicate gems into a higher tier gem.", NamedTextColor.GRAY), 160))
                        .get(),
                (m, e) -> GemMergeMenu.open(player, () -> open(player, item))
        );

        menu.setItem(4, 4, Menu.MENU_BACK, (m, e) -> NewItemEditorMenu.open(player, item));
        menu.openForPlayer(player);
    }

    private static org.bukkit.inventory.ItemStack lockedSlotItem(int slot) {
        List<Component> lore = new ArrayList<>(WordWrap.wrap(
                Component.text("Unlock this socket so it can hold a gem.", NamedTextColor.GRAY), 160
        ));
        lore.addAll(PvEUtils.getCostLore(NewItemTier.GEM_SLOT_UNLOCK_COST, true));
        lore.add(Component.empty());
        lore.add(Component.textOfChildren(
                Component.text("CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                Component.text("to unlock this socket", NamedTextColor.GRAY)
        ));
        return new ItemBuilder(Material.GRAY_DYE)
                .name(Component.text("Locked Socket " + (slot + 1), NamedTextColor.DARK_GRAY))
                .lore(lore)
                .get();
    }

    private static org.bukkit.inventory.ItemStack emptySlotItem(int slot) {
        return new ItemBuilder(Material.GLASS)
                .name(Component.text("Empty Socket " + (slot + 1), NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text("Click to socket one of your gems.", NamedTextColor.GRAY), 160))
                .get();
    }

    private static org.bukkit.inventory.ItemStack socketedSlotItem(int slot, Gem gem) {
        List<Component> lore = new ArrayList<>();
        lore.add(gem.getAttributeComponent());
        lore.add(Component.empty());
        lore.add(Component.textOfChildren(
                Component.text("LEFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                Component.text("to swap this gem", NamedTextColor.GRAY)
        ));
        lore.add(Component.textOfChildren(
                Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                Component.text("to remove this gem", NamedTextColor.GRAY)
        ));
        return new ItemBuilder(gem.getItem())
                .name(Component.text("Socket " + (slot + 1) + ": ", NamedTextColor.GRAY).append(gem.getColoredName()))
                .lore(lore)
                .glow()
                .get();
    }

    private static void openUnlockConfirmation(Player player, DatabasePlayer databasePlayer, NewItem item, int slot) {
        if (!item.canUnlockGemSlot() || slot != item.getUnlockedGemSlots()) {
            player.sendMessage(Component.text("You must unlock sockets in order!", NamedTextColor.RED));
            return;
        }
        Map<Spendable, Long> cost = NewItemTier.GEM_SLOT_UNLOCK_COST;
        if (!PvEUtils.hasEnough(player, databasePlayer, cost, "to unlock this socket")) {
            return;
        }

        List<Component> confirmLore = new ArrayList<>(WordWrap.wrap(
                Component.text("Unlock socket " + (slot + 1) + " on ", NamedTextColor.GRAY)
                         .append(item.getName())
                         .append(Component.text(".", NamedTextColor.GRAY)),
                160
        ));
        confirmLore.addAll(PvEUtils.getCostLore(cost, true));

        Menu.openConfirmationMenu(player,
                "Confirm Socket Unlock",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m, e) -> {
                    if (!PvEUtils.hasEnough(player, databasePlayer, cost, "to unlock this socket")) {
                        return;
                    }
                    cost.forEach((spendable, amount) -> spendable.subtractFromPlayer(databasePlayer, amount));
                    item.unlockGemSlot();
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                    NewItemsUtils.sendItemMessage(player, Component.text("You unlocked socket " + (slot + 1) + " on ", NamedTextColor.GRAY)
                                                                   .append(item.getHoverComponent())
                    );
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 2, 1.5f);
                    open(player, item);
                },
                (m, e) -> open(player, item),
                (m) -> m.setItem(4, 1, item.getItemBuilder().get(), Menu.ACTION_DO_NOTHING)
        );
    }

    private static void unsocket(Player player, DatabasePlayer databasePlayer, NewItem item, int slot, Gem gem) {
        if (item.unsocketGem(slot) == null) {
            return;
        }
        gem.addToPlayer(databasePlayer, 1);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        NewItemsUtils.sendItemMessage(player, Component.text("You removed ", NamedTextColor.GRAY)
                                                       .append(gem.getColoredName())
                                                       .append(Component.text(" from ", NamedTextColor.GRAY))
                                                       .append(item.getHoverComponent())
        );
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2, 0.8f);
        open(player, item);
    }

    static void socket(Player player, DatabasePlayer databasePlayer, NewItem item, int slot, Gem gem) {
        if (gem.getFromPlayer(databasePlayer) < 1) {
            player.sendMessage(Component.text("You do not own that gem!", NamedTextColor.RED));
            return;
        }
        gem.subtractFromPlayer(databasePlayer, 1);
        Gem previous = item.socketGem(slot, gem);
        if (previous != null) {
            previous.addToPlayer(databasePlayer, 1);
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        NewItemsUtils.sendItemMessage(player, Component.text("You socketed ", NamedTextColor.GRAY)
                                                       .append(gem.getColoredName())
                                                       .append(Component.text(" into ", NamedTextColor.GRAY))
                                                       .append(item.getHoverComponent())
        );
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
        open(player, item);
    }

    /**
     * Lists every gem the player currently owns so one can be placed into a socket.
     */
    private static class GemSelectMenu {

        private static void open(Player player, DatabasePlayer databasePlayer, NewItem item, int slot) {
            Menu menu = new Menu("Select a Gem", 9 * 4);

            Map<Gem, Long> owned = new LinkedHashMap<>();
            for (Gem gem : Gem.VALUES) {
                long amount = databasePlayer.getPveStats().getGems(gem);
                if (amount > 0) {
                    owned.put(gem, amount);
                }
            }

            if (owned.isEmpty()) {
                menu.setItem(4, 1,
                        new ItemBuilder(Material.BARRIER)
                                .name(Component.text("No Gems", NamedTextColor.RED))
                                .lore(WordWrap.wrap(Component.text("You do not own any gems. Gems are earned from raid reward caches.",
                                        NamedTextColor.GRAY
                                ), 160))
                                .get(),
                        Menu.ACTION_DO_NOTHING
                );
            }

            int index = 0;
            for (Map.Entry<Gem, Long> entry : owned.entrySet()) {
                Gem gem = entry.getKey();
                List<Component> lore = new ArrayList<>();
                lore.add(gem.getAttributeComponent());
                lore.add(Component.empty());
                lore.add(Component.text("Owned: ", NamedTextColor.GRAY)
                                  .append(Component.text(entry.getValue(), NamedTextColor.YELLOW)));
                lore.add(Component.empty());
                lore.add(Component.text("Click to socket this gem", NamedTextColor.YELLOW));
                menu.setItem(index % 9, index / 9,
                        new ItemBuilder(gem.getItem())
                                .name(gem.getColoredName())
                                .lore(lore)
                                .amount((int) Math.min(64, entry.getValue()))
                                .get(),
                        (m, e) -> NewItemGemMenu.socket(player, databasePlayer, item, slot, gem)
                );
                index++;
            }

            menu.setItem(4, 3, Menu.MENU_BACK, (m, e) -> NewItemGemMenu.open(player, item));
            menu.openForPlayer(player);
        }

    }

}

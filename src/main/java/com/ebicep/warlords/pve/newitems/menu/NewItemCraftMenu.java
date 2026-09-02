package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.events.ItemCraftEvent;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsManager;
import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.events.NewItemCraftEvent;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewItemCraftMenu {

    private static final int SETS_PER_PAGE = 9 * 5;

    public static void open(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu("Craft New Item", 9 * 4);

        int x = 3;
        for (NewItemTier tier : NewItemTier.VALUES) {
            Map<Spendable, Long> craftCost = tier.getCraftCost();
            if (craftCost == null || craftCost.isEmpty()) {
                continue;
            }

            Material icon = tier.getTerracotaMaterial() != null ? tier.getTerracotaMaterial() : Material.BOOK;
            menu.setItem(x, 1, new ItemBuilder(icon)
                            .name(Component.text(tier.getName(), tier.getTextColor()))
                            .lore(PvEUtils.getCostLore(craftCost, null, false))
                            .get(),
                    (m, e) -> handleClick(player, databasePlayer, tier, craftCost)
            );
            x += 2;
        }

        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static void handleClick(Player player, DatabasePlayer databasePlayer, NewItemTier tier, Map<Spendable, Long> craftCost) {
        for (Map.Entry<Spendable, Long> entry : craftCost.entrySet()) {
            Spendable spendable = entry.getKey();
            long amount = entry.getValue();
            if (spendable.getFromPlayer(databasePlayer) < amount) {
                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                            .append(spendable.getCostColoredName(amount))
                                            .append(Component.text(" to craft this item!")));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                return;
            }
        }

        if (tier == NewItemTier.LEGENDARY || tier == NewItemTier.SOVEREIGN) {
            openSetSelection(player, databasePlayer, tier, craftCost, 1);
            return;
        }

        openConfirmation(player, databasePlayer, tier, craftCost, null, null);
    }

    private static void openSetSelection(
            Player player,
            DatabasePlayer databasePlayer,
            NewItemTier tier,
            Map<Spendable, Long> craftCost,
            int page
    ) {
        Menu menu = new Menu("Choose " + tier.getName() + " Set", 9 * 6);
        Set<NewItemsSetBonus> tierSets = NewItemsSetBonus.BY_TIER.get(tier);
        List<NewItemsSetBonus> setBonuses = tierSets == null
                ? List.of()
                : tierSets.stream()
                          .filter(setBonus -> setBonus.getSlots() != null && !setBonus.getSlots().isEmpty())
                          .sorted(Comparator.comparing(NewItemsSetBonus::getName))
                          .toList();

        for (int i = 0; i < SETS_PER_PAGE; i++) {
            int setIndex = i + (page - 1) * SETS_PER_PAGE;
            if (setIndex >= setBonuses.size()) {
                break;
            }
            NewItemsSetBonus setBonus = setBonuses.get(setIndex);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.addAll(setBonus.getDescriptionLore());
            lore.add(Component.empty());
            lore.add(Component.text("Available Pieces:", NamedTextColor.GRAY));
            for (NewItemsSlot slot : setBonus.getSlots()) {
                lore.add(Component.text(" - " + slot.getName(), NamedTextColor.GRAY));
            }

            menu.setItem(
                    i % 9,
                    i / 9,
                    new ItemBuilder(setBonus.getSlots().getFirst().getMaterial())
                            .name(Component.text(setBonus.getName(), tier.getTextColor()))
                            .lore(lore)
                            .get(),
                    (m, e) -> openPieceSelection(player, databasePlayer, tier, craftCost, setBonus, page)
            );
        }

        if (page > 1) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openSetSelection(player, databasePlayer, tier, craftCost, page - 1)
            );
        }
        if (setBonuses.size() > page * SETS_PER_PAGE) {
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openSetSelection(player, databasePlayer, tier, craftCost, page + 1)
            );
        }

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> open(player));
        menu.openForPlayer(player);
    }

    private static void openPieceSelection(
            Player player,
            DatabasePlayer databasePlayer,
            NewItemTier tier,
            Map<Spendable, Long> craftCost,
            NewItemsSetBonus setBonus,
            int setPage
    ) {
        Menu menu = new Menu("Choose " + setBonus.getName() + " Piece", 9 * 3);
        List<NewItemsSlot> slots = setBonus.getSlots();
        int startX = Math.max(0, (9 - slots.size()) / 2);

        for (int i = 0; i < slots.size(); i++) {
            NewItemsSlot slot = slots.get(i);
            menu.setItem(
                    startX + i,
                    1,
                    new ItemBuilder(slot.getMaterial())
                            .name(Component.text(setBonus.getName() + " " + slot.getName(), tier.getTextColor()))
                            .get(),
                    (m, e) -> openConfirmation(player, databasePlayer, tier, craftCost, setBonus, slot)
            );
        }

        menu.setItem(4, 2, Menu.MENU_BACK, (m, e) -> openSetSelection(player, databasePlayer, tier, craftCost, setPage));
        menu.openForPlayer(player);
    }

    private static void openConfirmation(
            Player player,
            DatabasePlayer databasePlayer,
            NewItemTier tier,
            Map<Spendable, Long> craftCost,
            NewItemsSetBonus setBonus,
            NewItemsSlot slot
    ) {
        List<Component> confirmLore = new ArrayList<>();
        confirmLore.add(Component.text("Tier: ", NamedTextColor.GRAY).append(Component.text(tier.getName(), tier.getTextColor())));
        if (setBonus != null && slot != null) {
            confirmLore.add(Component.text("Set: ", NamedTextColor.GRAY).append(Component.text(setBonus.getName(), tier.getTextColor())));
            confirmLore.add(Component.text("Piece: ", NamedTextColor.GRAY).append(Component.text(slot.getName(), tier.getTextColor())));
        }
        confirmLore.addAll(PvEUtils.getCostLore(craftCost, "Cost", true));

        Menu.openConfirmationMenu(player,
                "Confirm Craft",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m2, e2) -> {
                    for (Map.Entry<Spendable, Long> entry : craftCost.entrySet()) {
                        entry.getKey().subtractFromPlayer(databasePlayer, entry.getValue());
                    }
                    NewItem item = setBonus == null || slot == null
                            ? NewItemsUtils.generateRandomItem(tier)
                            : new NewItem(setBonus, slot);
                    NewItemsManager itemsManager = databasePlayer.getPveStats().getNewItemsManager();
                    itemsManager.addItem(item);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    NewItemsUtils.sendItemMessage(player, Component.text("You crafted ", NamedTextColor.GRAY).append(item.getHoverComponent()));
                    Utils.playGlobalSound(player.getLocation(), "misc.itemcraft", 500, 0.5f);
                    Bukkit.getServer().getPluginManager().callEvent(new NewItemCraftEvent(player.getUniqueId(), item));
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.sendMessage(Permissions.getPrefixWithColor(player, false)
                                .append(Component.text(player.getName()))
                                .append(Component.text(" crafted ", NamedTextColor.GRAY))
                                .append(item.getHoverComponent())
                                .append(Component.text("!"))
                        );
                    }
                    EffectUtils.strikeLightning(player.getLocation(), false);
                    player.closeInventory();
                },
                (m2, e2) -> {
                    if (setBonus != null) {
                        openPieceSelection(player, databasePlayer, tier, craftCost, setBonus, 1);
                    } else {
                        open(player);
                    }
                },
                (m2) -> {}
        );
    }

}

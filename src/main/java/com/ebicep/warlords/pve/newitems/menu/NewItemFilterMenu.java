package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.MenuUtils;
import com.ebicep.warlords.pve.newitems.NewItemsManager;
import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class NewItemFilterMenu {

    public static void openItemFilterMenu(Player player, DatabasePlayer databasePlayer, BiConsumer<Menu, InventoryClickEvent> backAction) {
        Menu menu = new Menu("Item Filter", 9 * 4);

        //attributes
        //tier
        //type
        //favorite

        NewItemsManager itemsManager = databasePlayer.getPveStats().getNewItemsManager();
        NewItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings menuSettings = itemsManager.getMenuFilterSettings();

        EnumSet<NewItemAttribute> attributeFilter = menuSettings.getAttributeFilter();
        menu.setItem(1, 1,
                new ItemBuilder(attributeFilter.isEmpty() ? Material.BARRIER : Material.BOOK)
                        .name(Component.text("Attributes", NamedTextColor.GREEN))
                        .lore(
                                Arrays.stream(NewItemAttribute.VALUES)
                                      .map(value -> Component.text(value.getName(), attributeFilter.contains(value) ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                                      .collect(Collectors.toList())
                        )
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to change attribute filter", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
                    MenuUtils.openEnumSelectorMenu(player, "Attribute Filter", NewItemAttribute.VALUES, attributeFilter, backAction);
                }
        );

        NewItemTier tierFilter = menuSettings.getTierFilter();
        menu.setItem(2, 1,
                new ItemBuilder(tierFilter == null ? Material.WHITE_TERRACOTTA : tierFilter.getTerracotaMaterial())
                        .name(Component.text("Tier", NamedTextColor.GREEN))
                        .lore(Component.text("None", tierFilter == null ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                        .addLore(
                                Arrays.stream(NewItemTier.VALUES)
                                      .map(value -> Component.text(value.getName(), tierFilter == value ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                                      .collect(Collectors.toList())
                        )
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to change tier filter", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
//                    menuSettings.setTierFilter(tierFilter.next());
                    if (tierFilter == null) {
                        menuSettings.setTierFilter(NewItemTier.COMMON);
                    } else if (tierFilter == NewItemTier.VALUES[NewItemTier.VALUES.length - 1]) {
                        menuSettings.setTierFilter(null);
                    } else {
                        menuSettings.setTierFilter(tierFilter.next());
                    }
                    openItemFilterMenu(player, databasePlayer, backAction);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
        );

        NewItemsSlot typeFilter = menuSettings.getSlotFilter();
        menu.setItem(3, 1,
                new ItemBuilder(typeFilter == null ? Material.BARRIER : typeFilter.getMaterial())
                        .name(Component.text("Slot", NamedTextColor.GREEN))
                        .lore(Component.text("Any", typeFilter == null ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                        .addLore(
                                Arrays.stream(NewItemsSlot.VALUES)
                                      .map(value -> Component.text(value.getName(), typeFilter == value ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                                      .collect(Collectors.toList())
                        )
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to change type filter", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
                    if (typeFilter == null) {
                        menuSettings.setSlotFilter(NewItemsSlot.VALUES[0]);
                    } else if (typeFilter == NewItemsSlot.VALUES[NewItemsSlot.VALUES.length - 1]) {
                        menuSettings.setSlotFilter(null);
                    } else {
                        menuSettings.setSlotFilter(typeFilter.next());
                    }
                    openItemFilterMenu(player, databasePlayer, backAction);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
        );

        boolean favoriteFilter = menuSettings.getFavoriteFilter();
        menu.setItem(4, 1,
                new ItemBuilder(!favoriteFilter ? new ItemStack(Material.BARRIER) : new ItemStack(Material.DIAMOND))
                        .name(Component.text("Modifier", NamedTextColor.GREEN))
                        .lore(
                                Component.text("None", !favoriteFilter ? NamedTextColor.AQUA : NamedTextColor.GRAY),
                                Component.text("Favorites", !favoriteFilter ? NamedTextColor.GRAY : NamedTextColor.AQUA),
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to change favorite filter", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.nextFavoriteFilter();
                    openItemFilterMenu(player, databasePlayer, backAction);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
        );

        menu.setItem(4, 3, Menu.MENU_BACK, backAction);
        menu.openForPlayer(player);
    }


}

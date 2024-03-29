package com.ebicep.warlords.pve.items.menu.util;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ItemFilterMenu {

    public static void openItemFilterMenu(Player player, DatabasePlayer databasePlayer, BiConsumer<Menu, InventoryClickEvent> backAction) {
        Menu menu = new Menu("Item Filter", 9 * 4);

        //stat pool
        //tier
        //type
        //modifier
        //selected bonus
        //favorite

        ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
        ItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings menuSettings = itemsManager.getMenuFilterSettings();

        EnumSet<BasicStatPool> statPoolFilter = menuSettings.getStatPoolFilter();
        menu.setItem(1, 1,
                new ItemBuilder(statPoolFilter.isEmpty() ? Material.BARRIER : Material.BOOK)
                        .name(Component.text("Stat Pool", NamedTextColor.GREEN))
                        .lore(
                                Arrays.stream(BasicStatPool.VALUES)
                                      .map(value -> Component.text(value.name, statPoolFilter.contains(value) ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                                      .collect(Collectors.toList())
                        )
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to change stat pool filter", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
                    openStatPoolFilterMenu(player, databasePlayer, menuSettings, backAction);
                }
        );

        ItemTier tierFilter = menuSettings.getTierFilter();
        menu.setItem(2, 1,
                new ItemBuilder(tierFilter.clayBlock)
                        .name(Component.text("Tier", NamedTextColor.GREEN))
                        .lore(
                                Arrays.stream(ItemTier.VALUES)
                                      .map(value -> Component.text(value.name, tierFilter == value ? NamedTextColor.AQUA : NamedTextColor.GRAY))
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
                    menuSettings.setTierFilter(tierFilter.next());
                    openItemFilterMenu(player, databasePlayer, backAction);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
        );

        ItemType typeFilter = menuSettings.getTypeFilter();
        menu.setItem(3, 1,
                new ItemBuilder(typeFilter.skull)
                        .name(Component.text("Type", NamedTextColor.GREEN))
                        .lore(
                                Arrays.stream(ItemType.VALUES)
                                      .map(value -> Component.text(value.name, typeFilter == value ? NamedTextColor.AQUA : NamedTextColor.GRAY))
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
                    menuSettings.setTypeFilter(typeFilter.next());
                    openItemFilterMenu(player, databasePlayer, backAction);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
        );

        ItemSearchMenu.ModifierFilter modifierFilter = menuSettings.getModifierFilter();
        menu.setItem(4, 1,
                new ItemBuilder(modifierFilter.itemStack)
                        .name(Component.text("Modifier", NamedTextColor.GREEN))
                        .lore(
                                Arrays.stream(ItemSearchMenu.ModifierFilter.VALUES)
                                      .map(value -> Component.text(value.name, modifierFilter == value ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                                      .collect(Collectors.toList())
                        )
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to change modifier filter", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setModifierFilter(modifierFilter.next());
                    openItemFilterMenu(player, databasePlayer, backAction);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
        );

        boolean addonFilter = menuSettings.getAddonFilter();
        Specializations lastSpec = databasePlayer.getLastSpec();
        menu.setItem(5, 1,
                new ItemBuilder(!addonFilter ? new ItemStack(Material.BARRIER) : Specializations.getClass(lastSpec).item)
                        .name(Component.text("Modifier", NamedTextColor.GREEN))
                        .lore(
                                Component.text("None", !addonFilter ? NamedTextColor.AQUA : NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Selected Spec (" + lastSpec.name + ")", !addonFilter ? NamedTextColor.GRAY : NamedTextColor.AQUA),
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to change bonus filter", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.nextAddonFilter();
                    openItemFilterMenu(player, databasePlayer, backAction);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
        );

        boolean favoriteFilter = menuSettings.getFavoriteFilter();
        menu.setItem(6, 1,
                new ItemBuilder(!favoriteFilter ? new ItemStack(Material.BARRIER) : new ItemStack(Material.DIAMOND))
                        .name(Component.text("Modifier", NamedTextColor.GREEN))
                        .lore(
                                Component.text("None", !favoriteFilter ? NamedTextColor.AQUA : NamedTextColor.GRAY),
                                Component.empty(),
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

    private static void openStatPoolFilterMenu(
            Player player,
            DatabasePlayer databasePlayer,
            ItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings menuFilterSettings,
            BiConsumer<Menu, InventoryClickEvent> backAction
    ) {
        Menu menu = new Menu("Stat Pool Filter", 5 * 9);

        EnumSet<BasicStatPool> statPoolFilter = menuFilterSettings.getStatPoolFilter();
        BasicStatPool[] values = BasicStatPool.VALUES;
        for (int i = 0; i < values.length; i++) {
            BasicStatPool stat = values[i];
            ItemBuilder itemBuilder = new ItemBuilder(Utils.getWoolFromIndex(i))
                    .name(Component.text(stat.name, NamedTextColor.GREEN));
            boolean filtered = statPoolFilter.contains(stat);
            if (filtered) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            menu.setItem(i % 7 + 1, i / 7 + 1,
                    itemBuilder
                            .get(),
                    (m, e) -> {
                        if (filtered) {
                            statPoolFilter.remove(stat);
                        } else {
                            statPoolFilter.add(stat);
                        }
                        openStatPoolFilterMenu(player, databasePlayer, menuFilterSettings, backAction);
                    }
            );
        }

        menu.setItem(4, 4, Menu.MENU_BACK, (m, e) -> openItemFilterMenu(player, databasePlayer, backAction));
        menu.openForPlayer(player);
    }


}

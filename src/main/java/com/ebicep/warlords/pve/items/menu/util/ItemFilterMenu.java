package com.ebicep.warlords.pve.items.menu.util;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ItemFilterMenu {

    public static void openItemFilterMenu(Player player, DatabasePlayer databasePlayer, BiConsumer<Menu, InventoryClickEvent> backAction) {
        Menu menu = new Menu("Item Filter", 9 * 4);

        //tier
        //type
        //modifier
        //selected bonus
        //favorite

        ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
        ItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings menuSettings = itemsManager.getMenuFilterSettings();

        ItemTier tierFilter = menuSettings.getTierFilter();
        menu.setItem(2, 1,
                new ItemBuilder(tierFilter.clayBlock)
                        .name(Component.text("Tier", NamedTextColor.GREEN))
                        .loreLEGACY(Arrays.stream(ItemTier.VALUES)
                                          .map(value -> (tierFilter == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                          .collect(Collectors.joining("\n")),
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK " + ChatColor.GREEN + "to change tier filter"
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
                        .loreLEGACY(Arrays.stream(ItemType.VALUES)
                                          .map(value -> (typeFilter == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                          .collect(Collectors.joining("\n")),
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK " + ChatColor.GREEN + "to change type filter"
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
                        .loreLEGACY(Arrays.stream(ItemSearchMenu.ModifierFilter.VALUES)
                                          .map(value -> (modifierFilter == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                          .collect(Collectors.joining("\n")),
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK " + ChatColor.GREEN + "to change modifier filter"
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
                        .loreLEGACY(!addonFilter ? ChatColor.AQUA + "None\n" + ChatColor.GRAY + "Selected Spec (" + lastSpec.name + ")" :
                                    ChatColor.GRAY + "None\n" + ChatColor.AQUA + "Selected Spec (" + lastSpec.name + ")",
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK " + ChatColor.GREEN + "to change bonus filter"
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
                        .loreLEGACY(!favoriteFilter ? ChatColor.AQUA + "All\n" + ChatColor.GRAY + "Favorites" :
                                    ChatColor.GRAY + "All\n" + ChatColor.AQUA + "Favorites",
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK " + ChatColor.GREEN + "to change favorite filter"
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

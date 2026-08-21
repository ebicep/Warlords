package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.MenuUtils;
import com.ebicep.warlords.pve.newitems.NewItemLoreCreator;
import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class NewItemSetsMenu {

    private static final int SETS_PER_PAGE = 9 * 5;

    public static void open(Player player) {
        open(
                player,
                1,
                EnumSet.noneOf(NewItemAttribute.class),
                EnumSet.noneOf(NewItemTier.class),
                EnumSet.noneOf(NewItemsSlot.class),
                SortingOption.NAME,
                true
        );
    }

    private static void open(
            Player player,
            int page,
            EnumSet<NewItemAttribute> attributeFilter,
            EnumSet<NewItemTier> tierFilter,
            EnumSet<NewItemsSlot> slotFilter,
            SortingOption sortingOption,
            boolean ascending
    ) {
        Menu menu = new Menu("Item Sets", 9 * 6);
        List<NewItemsSetBonus> filteredSetBonuses = Arrays
                .stream(NewItemsSetBonus.VALUES)
                .filter(setBonus -> {
                    boolean matchesAttribute = attributeFilter.isEmpty() || setBonus
                            .getAttributes()
                            .keySet()
                            .stream()
                            .anyMatch(attributeFilter::contains);
                    boolean matchesTier = tierFilter.isEmpty() || tierFilter.contains(setBonus.getTier());
                    boolean matchesSlot = slotFilter.isEmpty() || setBonus
                            .getSlots()
                            .stream()
                            .anyMatch(slotFilter::contains);
                    return matchesAttribute && matchesTier && matchesSlot;
                })
                .sorted(ascending ? sortingOption.comparator : sortingOption.comparator.reversed())
                .toList();

        for (int i = 0; i < SETS_PER_PAGE; i++) {
            int setIndex = i + (page - 1) * SETS_PER_PAGE;
            if (setIndex >= filteredSetBonuses.size()) {
                break;
            }
            NewItemsSetBonus setBonus = filteredSetBonuses.get(setIndex);
            try {
                List<Component> lore = new NewItemLoreCreator.Builder(setBonus)
                        .addStarComponent()
                        .addBasicAttributes()
                        .addBonusAttributes()
                        .addSetBonus()
                        .build();
                menu.setItem(
                        i % 9, i / 9,
                        new ItemBuilder(setBonus.getSlots().getFirst().getMaterial())
                                .name(Component.text(setBonus.getName(), setBonus.getTier().getTextColor()))
                                .lore(lore)
                                .get(),
                        (m, e) -> {}
                );
            } catch (Exception e) {
                ChatUtils.MessageType.NEW_ITEMS.sendErrorMessage(new Throwable("Error opening New Item Sets Menu for " + setBonus));
            }
        }

        BiConsumer<Menu, InventoryClickEvent> backAction = (m2, e2) ->
                open(player, 1, attributeFilter, tierFilter, slotFilter, sortingOption, ascending);

        menu.setItem(1, 5,
                new ItemBuilder(Material.HOPPER)
                        .name(Component.text("Attributes", NamedTextColor.GREEN))
                        .lore(
                                Arrays.stream(NewItemAttribute.BASIC_ATTRIBUTES)
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
                    MenuUtils.openEnumSelectorMenu(
                            player,
                            "Attribute Filter",
                            NewItemAttribute.BASIC_ATTRIBUTES,
                            attributeFilter,
                            NewItemAttribute::getItemStack,
                            backAction
                    );
                }
        );
        menu.setItem(2, 5,
                new ItemBuilder(Material.HOPPER)
                        .name(Component.text("Tiers", NamedTextColor.GREEN))
                        .lore(
                                Arrays.stream(NewItemTier.VALUES)
                                      .map(value -> Component.text(value.getName(), tierFilter.contains(value) ? NamedTextColor.AQUA : NamedTextColor.GRAY))
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
                    MenuUtils.openEnumSelectorMenu(
                            player,
                            "Tier Filter",
                            NewItemTier.VALUES,
                            tierFilter,
                            tier -> Utils.getWoolFromIndex(tier.ordinal()),
                            backAction
                    );
                }
        );
        menu.setItem(3, 5,
                new ItemBuilder(Material.HOPPER)
                        .name(Component.text("Slots", NamedTextColor.GREEN))
                        .lore(
                                Arrays.stream(NewItemsSlot.VALUES)
                                      .map(value -> Component.text(value.getName(), slotFilter.contains(value) ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                                      .collect(Collectors.toList())
                        )
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to change slot filter", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
                    MenuUtils.openEnumSelectorMenu(
                            player,
                            "Slot Filter",
                            NewItemsSlot.VALUES,
                            slotFilter,
                            slot -> Utils.getWoolFromIndex(slot.ordinal()),
                            backAction
                    );
                }
        );
        menu.setItem(5, 5,
                new ItemBuilder(Material.COMPARATOR)
                        .name(Component.text("Sort By", NamedTextColor.GREEN))
                        .lore(Arrays.stream(SortingOption.VALUES)
                                    .map(value -> Component.text(value.name, (sortingOption == value ? NamedTextColor.AQUA : NamedTextColor.GRAY)))
                                    .collect(Collectors.toList())
                        )
                        .get(),
                (m, e) -> {
                    SortingOption newSortingOption = sortingOption.next();
                    open(player, 1, attributeFilter, tierFilter, slotFilter, newSortingOption, ascending);
                }
        );
        menu.setItem(6, 5,
                new ItemBuilder(Material.LEVER)
                        .name(Component.text("Sort Order", NamedTextColor.GREEN))
                        .lore(
                                Component.text("Ascending", ascending ? NamedTextColor.AQUA : NamedTextColor.GRAY),
                                Component.text("Descending", ascending ? NamedTextColor.GRAY : NamedTextColor.AQUA)
                        )
                        .get(),
                (m, e) -> {
                    open(player, 1, attributeFilter, tierFilter, slotFilter, sortingOption, !ascending);
                }
        );
        menu.setItem(7, 5,
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(Component.text("Reset Settings", NamedTextColor.GREEN))
                        .lore(Component.text("Reset filters and sorting", NamedTextColor.GRAY))
                        .get(),
                (m, e) -> open(player)
        );


        if (page > 1) {
            menu.setItem(
                    0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> open(player, page - 1, attributeFilter, tierFilter, slotFilter, sortingOption, ascending)
            );
        }
        if (filteredSetBonuses.size() > page * SETS_PER_PAGE) {
            menu.setItem(
                    8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> open(player, page + 1, attributeFilter, tierFilter, slotFilter, sortingOption, ascending)
            );
        }

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private enum SortingOption {
        NAME("Name", Comparator.comparing(NewItemsSetBonus::getName)),
        TIER("Tier", Comparator.comparing(NewItemsSetBonus::getTier)),
//        SLOT(Comparator.comparing(setBonus -> setBonus.getSlots().getFirst()))
        ;

        public static final SortingOption[] VALUES = values();
        private final String name;
        private final Comparator<NewItemsSetBonus> comparator;

        SortingOption(String name, Comparator<NewItemsSetBonus> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        public SortingOption next() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }

    }

}

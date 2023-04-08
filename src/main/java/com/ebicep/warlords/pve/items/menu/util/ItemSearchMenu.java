package com.ebicep.warlords.pve.items.menu.util;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.items.ItemLoadout;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.addons.ItemAddonSpecBonus;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.TriConsumer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ItemSearchMenu extends Menu {
    private final Player player;
    private final TriConsumer<AbstractItem, Menu, InventoryClickEvent> itemClickAction;
    private final UnaryOperator<ItemBuilder> editItem;
    private final PlayerItemMenuSettings menuSettings;
    private final DatabasePlayer databasePlayer;
    private Consumer<Menu> menu;

    public ItemSearchMenu(
            Player player,
            String name,
            TriConsumer<AbstractItem, Menu, InventoryClickEvent> itemClickAction,
            UnaryOperator<ItemBuilder> editItem,
            PlayerItemMenuSettings menuSettings,
            DatabasePlayer databasePlayer,
            Consumer<Menu> menu
    ) {
        this(player, name, itemClickAction, editItem, menuSettings, databasePlayer);
        this.menu = menu;
        menu.accept(this);
    }

    public ItemSearchMenu(
            Player player,
            String name,
            TriConsumer<AbstractItem, Menu, InventoryClickEvent> itemClickAction,
            UnaryOperator<ItemBuilder> editItem,
            PlayerItemMenuSettings menuSettings,
            DatabasePlayer databasePlayer
    ) {
        super(name, 9 * 6);
        this.player = player;
        this.itemClickAction = itemClickAction;
        this.editItem = editItem;
        this.menuSettings = menuSettings;
        this.databasePlayer = databasePlayer;
    }

    public void open() {
        super.clear();
        menuSettings.sort();
        addItems();
        addMobDrops();
        addResetSetting();
        addFilterBySetting();
        addSortBySetting();
        addSortOrderSetting();
        addPageArrows();
        if (menu != null) {
            menu.accept(this);
        }
        super.openForPlayer(player);
    }

    private void addItems() {
        List<UUID> equippedItems = databasePlayer.getPveStats()
                                                 .getItemsManager()
                                                 .getLoadouts()
                                                 .stream()
                                                 .map(ItemLoadout::getItems)
                                                 .flatMap(Collection::stream)
                                                 .collect(Collectors.toList());
        int page = menuSettings.getPage();
        List<AbstractItem> itemInventory = new ArrayList<>(menuSettings.getSortedItemInventory());
        int x = 0;
        int y = 0;
        for (int i = 0; i < 45; i++) {
            int itemNumber = ((page - 1) * 45) + i;
            if (itemNumber < itemInventory.size()) {
                AbstractItem item = itemInventory.get(itemNumber);
                ItemBuilder itemBuilder = editItem.apply(item.generateItemBuilder());
                if (equippedItems.contains(item.getUUID())) {
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                }
                setItem(x, y,
                        itemBuilder.get(),
                        (m, e) -> itemClickAction.accept(item, m, e)
                );
                x++;
                if (x == 9) {
                    x = 0;
                    y++;
                }
            }
        }
    }

    private void addMobDrops() {
        setItem(2, 5,
                new ItemBuilder(Material.SKULL_ITEM, 1, (short) SkullType.ZOMBIE.ordinal())
                        .name("§aYour Drops")
                        .lore(
                                MobDrops.ZENITH_STAR.getCostColoredName(MobDrops.ZENITH_STAR.getFromPlayer(databasePlayer)),
                                Currencies.CELESTIAL_BRONZE.getCostColoredName(Currencies.CELESTIAL_BRONZE.getFromPlayer(databasePlayer)),
                                Currencies.SCRAP_METAL.getCostColoredName(Currencies.SCRAP_METAL.getFromPlayer(databasePlayer))
                        )
                        .get(),
                (m, e) -> {}
        );
    }

    private void addResetSetting() {
        setItem(3, 5,
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(ChatColor.GREEN + "Reset Settings")
                        .lore(ChatColor.GRAY + "Reset the filter, sort, and order of weapons")
                        .get(),
                (m, e) -> {
                    menuSettings.reset();
                    open();
                }
        );
    }

    private void addFilterBySetting() {
        ItemTier filterBy = menuSettings.getTierFilter();
        int addonFilter = menuSettings.getAddonFilter();
        ModifierFilter modifierFilter = menuSettings.getModifierFilter();
        StringBuilder addonFilterLore = new StringBuilder();
        String[] addonFilters = PlayerItemMenuSettings.ADDON_FILTERS;
        for (int i = 0; i < addonFilters.length; i++) {
            String filter = addonFilters[i];
            addonFilterLore.append(addonFilter == i ? ChatColor.AQUA : ChatColor.GRAY).append(filter).append("\n");
        }
        setItem(5, 5,
                new ItemBuilder(Material.HOPPER)
                        .name(ChatColor.GREEN + "Filter By")
                        .lore(
                                Arrays.stream(ItemTier.VALUES)
                                      .map(value -> (filterBy == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                      .collect(Collectors.joining("\n")),
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK " + ChatColor.GREEN + "to change tier filter",
                                "",
                                Arrays.stream(ModifierFilter.VALUES)
                                      .map(value -> (modifierFilter == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                      .collect(Collectors.joining("\n")),
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK " + ChatColor.GREEN + "to change modifier filter",
                                "",
                                addonFilterLore +
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "SHIFT-CLICK " + ChatColor.GREEN + "to change bonus filter"
                        )
                        .get(),
                (m, e) -> {
                    if (e.isShiftClick()) {
                        menuSettings.nextAddonFilter();
                    } else if (e.isLeftClick()) {
                        menuSettings.setTierFilter(filterBy.next());
                    } else if (e.isRightClick()) {
                        menuSettings.setModifierFilter(modifierFilter.next());
                    }
                    menuSettings.setPage(1);
                    open();
                }
        );
    }

    private void addSortBySetting() {
        SortOptions sortedBy = menuSettings.getSortOption();
        setItem(6, 5,
                new ItemBuilder(Material.REDSTONE_COMPARATOR)
                        .name(ChatColor.GREEN + "Sort By")
                        .lore(Arrays.stream(SortOptions.VALUES)
                                    .map(value -> (sortedBy == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                    .collect(Collectors.joining("\n"))
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setSortOption(sortedBy.next());
                    open();
                }
        );
    }

    private void addSortOrderSetting() {
        setItem(7, 5,
                new ItemBuilder(Material.LEVER)
                        .name(ChatColor.GREEN + "Sort Order")
                        .lore(menuSettings.isAscending() ?
                              ChatColor.AQUA + "Ascending\n" + ChatColor.GRAY + "Descending" :
                              ChatColor.GRAY + "Ascending\n" + ChatColor.AQUA + "Descending"
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setAscending(!menuSettings.isAscending());
                    open();
                }
        );
    }

    private void addPageArrows() {
        int page = menuSettings.getPage();
        List<AbstractItem> itemInventory = new ArrayList<>(menuSettings.getSortedItemInventory());
        if (page - 1 > 0) {
            setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page - 1);
                        open();
                    }
            );
        }
        if (itemInventory.size() > (page * 45)) {
            setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page + 1);
                        open();
                    }
            );
        }
    }

    public enum SortOptions {
        DATE("Date", Comparator.comparing(AbstractItem::getObtainedDate)),
        TIER("Tier", Comparator.comparing(AbstractItem::getTier)),
        ITEM_SCORE("Item Score", Comparator.comparing(AbstractItem::getItemScore));

        private static final SortOptions[] VALUES = values();
        public final String name;
        public final Comparator<AbstractItem> comparator;

        SortOptions(String name, Comparator<AbstractItem> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        public SortOptions next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }

    public enum ModifierFilter {
        NONE("None", item -> true),
        NORMAL("Normal", item -> item.getModifier() == 0),
        BLESSED("Blessed", item -> item.getModifier() > 0),
        CURSED("Cursed", item -> item.getModifier() < 0),

        ;

        private static final ModifierFilter[] VALUES = values();
        public final String name;
        public final Predicate<AbstractItem> filter;

        ModifierFilter(String name, Predicate<AbstractItem> filter) {
            this.name = name;
            this.filter = filter;
        }

        public ModifierFilter next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }

    public static class PlayerItemMenuSettings {
        public static final String[] ADDON_FILTERS = new String[]{"None", "Selected Spec", "Selected Class"};
        private final Specializations selectedSpec;
        private final Classes selectedClass;
        private boolean openedFromNPC = false;
        private int page = 1;
        private List<AbstractItem> itemInventory = new ArrayList<>();
        private List<AbstractItem> sortedItemInventory = new ArrayList<>();
        private ItemTier tierFilter = ItemTier.ALL;
        private ModifierFilter modifierFilter = ModifierFilter.NONE;
        private int addonFilter = 0; // 0 = none, 1 = spec, 2 = class
        private SortOptions sortOption = SortOptions.DATE;
        private boolean ascending = true; //ascending = smallest -> largest/recent

        public PlayerItemMenuSettings(Specializations selectedSpec) {
            this.selectedSpec = selectedSpec;
            this.selectedClass = Specializations.getClass(selectedSpec);
        }

        public PlayerItemMenuSettings(DatabasePlayer databasePlayer) {
            this.selectedSpec = databasePlayer.getLastSpec();
            this.selectedClass = Specializations.getClass(databasePlayer.getLastSpec());
            setItemInventory(new ArrayList<>(databasePlayer.getPveStats().getItemsManager().getItemInventory()));
        }

        public PlayerItemMenuSettings setItemInventory(List<AbstractItem> itemInventory) {
            this.itemInventory = itemInventory;
            this.sortedItemInventory = new ArrayList<>(itemInventory);
            return this;
        }

        public void reset() {
            this.page = 1;
            this.tierFilter = ItemTier.ALL;
            this.addonFilter = 0;
            this.sortOption = SortOptions.DATE;
            this.ascending = true;
        }

        public void sort() {
            sortedItemInventory = new ArrayList<>(itemInventory);
            if (tierFilter != ItemTier.ALL) {
                sortedItemInventory.removeIf(item -> item.getTier() != tierFilter);
            }
            if (modifierFilter != ModifierFilter.NONE) {
                sortedItemInventory.removeIf(weapon -> !modifierFilter.filter.test(weapon));
            }
            if (addonFilter != 0) {
                if (addonFilter == 1) {
                    sortedItemInventory.removeIf(item -> !(item instanceof ItemAddonSpecBonus && ((ItemAddonSpecBonus) item).getSpec() == selectedSpec));
                } else if (addonFilter == 2) {
                    sortedItemInventory.removeIf(item -> !(item instanceof ItemAddonClassBonus && ((ItemAddonClassBonus) item).getClasses() == selectedClass));
                }
            }
            sortedItemInventory.sort(sortOption.comparator);
            if (!ascending) {
                Collections.reverse(sortedItemInventory);
            }
        }

        public boolean isOpenedFromNPC() {
            return openedFromNPC;
        }

        public void setOpenedFromNPC(boolean openedFromNPC) {
            this.openedFromNPC = openedFromNPC;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<AbstractItem> getSortedItemInventory() {
            return sortedItemInventory;
        }

        public ItemTier getTierFilter() {
            return tierFilter;
        }

        public void setTierFilter(ItemTier tierFilter) {
            this.tierFilter = tierFilter;
        }

        public ModifierFilter getModifierFilter() {
            return modifierFilter;
        }

        public void setModifierFilter(ModifierFilter modifierFilter) {
            this.modifierFilter = modifierFilter;
        }

        public int getAddonFilter() {
            return addonFilter;
        }

        public void nextAddonFilter() {
            if (addonFilter == 2) {
                addonFilter = 0;
            } else {
                addonFilter++;
            }
        }

        public SortOptions getSortOption() {
            return sortOption;
        }

        public void setSortOption(SortOptions sortOption) {
            this.sortOption = sortOption;
        }

        public boolean isAscending() {
            return ascending;
        }

        public void setAscending(boolean ascending) {
            this.ascending = ascending;
        }
    }
}

package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemLoadout;
import com.ebicep.warlords.pve.newitems.NewItemsManager;
import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.TriConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class NewItemSearchMenu extends Menu {

    private final Player player;
    private final TriConsumer<NewItem, Menu, InventoryClickEvent> itemClickAction;
    private final UnaryOperator<ItemBuilder> editItem;
    private final PlayerItemMenuSettings menuSettings;
    private final DatabasePlayer databasePlayer;
    private Consumer<Menu> menu;

    public NewItemSearchMenu(
            Player player,
            String name,
            TriConsumer<NewItem, Menu, InventoryClickEvent> itemClickAction,
            UnaryOperator<ItemBuilder> editItem,
            PlayerItemMenuSettings menuSettings,
            DatabasePlayer databasePlayer,
            Consumer<Menu> menu
    ) {
        this(player, name, itemClickAction, editItem, menuSettings, databasePlayer);
        this.menu = menu;
        menu.accept(this);
    }

    public NewItemSearchMenu(
            Player player,
            String name,
            TriConsumer<NewItem, Menu, InventoryClickEvent> itemClickAction,
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
                                                 .getNewItemsManager()
                                                 .getLoadouts()
                                                 .stream()
                                                 .map(NewItemLoadout::getItems)
                                                 .flatMap(Collection::stream)
                                                 .toList();
        int page = menuSettings.getPage();
        List<NewItem> itemInventory = new ArrayList<>(menuSettings.getSortedItemInventory());
        int x = 0;
        int y = 0;
        for (int i = 0; i < 45; i++) {
            int itemNumber = ((page - 1) * 45) + i;
            if (itemNumber < itemInventory.size()) {
                NewItem item = itemInventory.get(itemNumber);
                ItemBuilder itemBuilder = item.getItemBuilder();
                if (equippedItems.contains(item.getUUID())) {
                    if (!item.isFavorite()) {
                        itemBuilder.addLore(Component.empty());
                    }
                    itemBuilder.addLore(Component.text("EQUIPPED", NamedTextColor.AQUA));
                }
                itemBuilder = editItem.apply(itemBuilder);
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
                new ItemBuilder(Material.ZOMBIE_HEAD)
                        .name(Component.text("Your Drops", NamedTextColor.GREEN))
                        .lore(
                                MobDrop.ZENITH_STAR.getCostColoredName(MobDrop.ZENITH_STAR.getFromPlayer(databasePlayer)),
                                Currencies.SCRAP_METAL.getCostColoredName(Currencies.SCRAP_METAL.getFromPlayer(databasePlayer)),
                                Currencies.ETHEREUM_CRYSTAL.getCostColoredName(Currencies.ETHEREUM_CRYSTAL.getFromPlayer(databasePlayer))
                        )
                        .get(),
                (m, e) -> {}
        );
    }

    private void addResetSetting() {
        setItem(3, 5,
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(Component.text("Reset Settings", NamedTextColor.GREEN))
                        .lore(Component.text("Reset the filter, sort, and order of weapons", NamedTextColor.GRAY))
                        .get(),
                (m, e) -> {
                    menuSettings.reset();
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    open();
                }
        );
    }

    private void addFilterBySetting() {
        PlayerItemMenuSettings.PlayerItemMenuFilterSettings filterSettings = menuSettings.getFilterSettings();
        List<Component> filterLore = new ArrayList<>();
        TextComponent grayDash = Component.text("- ", NamedTextColor.GRAY);
        if (!filterSettings.getAttributeFilter().isEmpty()) {
            filterLore.add(Component.text("Attributes", NamedTextColor.AQUA));
            for (NewItemAttribute attribute : filterSettings.getAttributeFilter()) {
                filterLore.add(grayDash.append(Component.text(attribute.getName(), NamedTextColor.GRAY)));
            }
        }
        if (filterSettings.getSlotFilter() != null) {
            filterLore.add(Component.text("Slot", NamedTextColor.AQUA));
            filterLore.add(grayDash.append(Component.text(filterSettings.getSlotFilter().getName(), NamedTextColor.GRAY)));
        }
        if (filterSettings.getTierFilter() != null) {
            filterLore.add(Component.text("Tier", NamedTextColor.AQUA));
            filterLore.add(grayDash.append(Component.text(filterSettings.getTierFilter().getName(), NamedTextColor.GRAY)));
        }
        if (filterSettings.getFavoriteFilter()) {
            filterLore.add(Component.text("Modifier", NamedTextColor.AQUA));
            filterLore.add(grayDash.append(Component.text("Only Favorites", NamedTextColor.GRAY)));
        }
        if (filterLore.isEmpty()) {
            filterLore.add(Component.text("No filters selected", NamedTextColor.GRAY));
        }
        filterLore.add(Component.empty());
        filterLore.add(Component.textOfChildren(
                Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                Component.text(" to change", NamedTextColor.GRAY)
        ));
        setItem(5, 5,
                new ItemBuilder(Material.HOPPER)
                        .name(Component.text("Filter Settings", NamedTextColor.GREEN))
                        .lore(filterLore)
                        .get(),
                (m, e) -> {
                    NewItemFilterMenu.openItemFilterMenu(player, databasePlayer, (m2, e2) -> open());
                }
        );
    }

    private void addSortBySetting() {
        SortOptions sortedBy = menuSettings.getSortOption();
        setItem(6, 5,
                new ItemBuilder(Material.COMPARATOR)
                        .name(Component.text("Sort By", NamedTextColor.GREEN))
                        .lore(Arrays.stream(SortOptions.VALUES)
                                    .map(value -> Component.text(value.name, (sortedBy == value ? NamedTextColor.AQUA : NamedTextColor.GRAY)))
                                    .collect(Collectors.toList())
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
                        .name(Component.text("Sort Order", NamedTextColor.GREEN))
                        .lore(
                                Component.text("Ascending", menuSettings.isAscending() ? NamedTextColor.AQUA : NamedTextColor.GRAY),
                                Component.text("Descending", menuSettings.isAscending() ? NamedTextColor.GRAY : NamedTextColor.AQUA)
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
        List<NewItem> itemInventory = new ArrayList<>(menuSettings.getSortedItemInventory());
        if (page - 1 > 0) {
            setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
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
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page + 1);
                        open();
                    }
            );
        }
    }

    public enum SortOptions {
        DATE("Date", Comparator.comparing(NewItem::getCreationTime)),
        TIER("Tier", Comparator.comparing(NewItem::getTier)),
        SLOT("Slot", Comparator.comparing(NewItem::getSlot)),
        ITEM_SCORE("Item Score", Comparator.comparing(NewItem::getItemScore)),

        ;

        private static final SortOptions[] VALUES = values();
        public final String name;
        public final Comparator<NewItem> comparator;

        SortOptions(String name, Comparator<NewItem> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        public SortOptions next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }

    public static class PlayerItemMenuSettings {

        private int page = 1;
        private List<NewItem> itemInventory = new ArrayList<>();
        private List<NewItem> sortedItemInventory = new ArrayList<>();
        private PlayerItemMenuFilterSettings filterSettings;
        private SortOptions sortOption = SortOptions.DATE;
        private boolean ascending = true; //ascending = smallest -> largest/recent

        public PlayerItemMenuSettings(DatabasePlayer databasePlayer) {
            NewItemsManager itemsManager = databasePlayer.getPveStats().getNewItemsManager();
            PlayerItemMenuFilterSettings playerItemMenuFilterSettings = itemsManager.getMenuFilterSettings();
            if (playerItemMenuFilterSettings == null) {
                playerItemMenuFilterSettings = new PlayerItemMenuFilterSettings();
                itemsManager.setMenuFilterSettings(playerItemMenuFilterSettings);
                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            }
            this.filterSettings = playerItemMenuFilterSettings;
            setItemInventory(new ArrayList<>(databasePlayer.getPveStats().getNewItemsManager().getItemInventory()));
        }

        public PlayerItemMenuSettings setItemInventory(List<NewItem> itemInventory) {
            this.itemInventory = itemInventory;
            this.sortedItemInventory = new ArrayList<>(itemInventory);
            return this;
        }

        public void reset() {
            this.page = 1;
            this.filterSettings.attributeFilter = EnumSet.noneOf(NewItemAttribute.class);
            this.filterSettings.tierFilter = null;
            this.filterSettings.slotFilter = null;
            this.filterSettings.favoriteFilter = false;
            this.sortOption = SortOptions.DATE;
            this.ascending = true;
        }

        public void sort() {
            sortedItemInventory = new ArrayList<>(itemInventory);
            if (!filterSettings.attributeFilter.isEmpty()) {
                sortedItemInventory.removeIf(item -> {
                    Set<NewItemAttribute> attributes = item.getAllAttributes();
                    for (NewItemAttribute attribute : filterSettings.attributeFilter) {
                        if (!attributes.contains(attribute)) {
                            return true;
                        }
                    }
                    return false;
                });
            }
            if (filterSettings.tierFilter != null) {
                sortedItemInventory.removeIf(item -> item.getTier() != filterSettings.tierFilter);
            }
            if (filterSettings.slotFilter != null) {
                sortedItemInventory.removeIf(item -> item.getSlot() != filterSettings.slotFilter);
            }
            if (filterSettings.favoriteFilter) {
                sortedItemInventory.removeIf(item -> !item.isFavorite());
            }
            sortedItemInventory.sort(sortOption.comparator);
            if (!ascending) {
                Collections.reverse(sortedItemInventory);
            }
        }

        public PlayerItemMenuFilterSettings getFilterSettings() {
            return filterSettings;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<NewItem> getSortedItemInventory() {
            return sortedItemInventory;
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

        public static class PlayerItemMenuFilterSettings {

            public EnumSet<NewItemAttribute> attributeFilter = EnumSet.noneOf(NewItemAttribute.class);
            @Nullable
            public NewItemTier tierFilter = null;
            @Nullable
            public NewItemsSlot slotFilter = null;
            private boolean favoriteFilter = false;

            public PlayerItemMenuFilterSettings() {
            }

            public EnumSet<NewItemAttribute> getAttributeFilter() {
                return attributeFilter;
            }

            @Nullable
            public NewItemTier getTierFilter() {
                return tierFilter;
            }

            public void setTierFilter(@Nullable NewItemTier tierFilter) {
                this.tierFilter = tierFilter;
            }

            @Nullable
            public NewItemsSlot getSlotFilter() {
                return slotFilter;
            }

            public void setSlotFilter(@Nullable NewItemsSlot slotFilter) {
                this.slotFilter = slotFilter;
            }

            public boolean getFavoriteFilter() {
                return favoriteFilter;
            }

            public void nextFavoriteFilter() {
                favoriteFilter = !favoriteFilter;
            }

        }

    }

}

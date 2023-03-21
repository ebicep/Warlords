package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.addons.ItemAddonSpecBonus;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class ItemsMenu {

    public static final HashMap<UUID, PlayerItemMenuSettings> PLAYER_MENU_SETTINGS = new HashMap<>();

    public static void openItemMenuExternal(Player player, boolean fromNPC) {
        UUID uuid = player.getUniqueId();
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
            List<AbstractItem<?, ?, ?>> itemInventory = new ArrayList<>(itemsManager.getItemInventory());

            PLAYER_MENU_SETTINGS.putIfAbsent(uuid, new PlayerItemMenuSettings());
            PlayerItemMenuSettings menuSettings = PLAYER_MENU_SETTINGS.get(uuid);
            menuSettings.setOpenedFromNPC(fromNPC);
            menuSettings.setItemInventory(itemInventory);
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(uuid);
            menuSettings.sort(playerSettings.getSelectedSpec(), Specializations.getClass(playerSettings.getSelectedSpec()));

            openItemMenuInternal(player, databasePlayer);
        });

    }

    public static void openItemMenuInternal(Player player, DatabasePlayer databasePlayer) {
        UUID uuid = player.getUniqueId();
        PLAYER_MENU_SETTINGS.putIfAbsent(uuid, new PlayerItemMenuSettings());
        PlayerItemMenuSettings menuSettings = PLAYER_MENU_SETTINGS.get(uuid);
        int page = menuSettings.getPage();
        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(uuid);
        menuSettings.sort(playerSettings.getSelectedSpec(), Specializations.getClass(playerSettings.getSelectedSpec()));
        List<AbstractItem<?, ?, ?>> itemInventory = new ArrayList<>(menuSettings.getSortedItemInventory());

        SortOptions sortedBy = menuSettings.getSortOption();
        ItemTier filterBy = menuSettings.getTierFilter();
        int addonFilter = menuSettings.getAddonFilter();

        Menu menu = new Menu("Items", 9 * 6);

        ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
        List<ItemLoadout> loadouts = itemsManager.getLoadouts();
        for (int i = 0, loadoutsSize = loadouts.size(); i < loadoutsSize; i++) {
            ItemLoadout loadout = loadouts.get(i);
            menu.addItem(new ItemBuilder(loadout.getName().equals("Default") ? Material.IRON_DOOR : Material.WOOD_DOOR)
                            .name(ChatColor.GREEN + "Loadout #" + (i + 1) + ": " + ChatColor.GOLD + loadout.getName())
                            .lore(
                                    ChatColor.GRAY + "Weight: " + ChatColor.GOLD + loadout.getWeight(itemsManager),
                                    ChatColor.GRAY + "Difficulty: " + ChatColor.GOLD + (loadout.getDifficulty() == null ? "Any" : loadout.getDifficulty()
                                                                                                                                         .getName()),
                                    ChatColor.GRAY + "Specialization: " + ChatColor.GOLD + (loadout.getSpec() == null ? "Any" : loadout.getSpec().name)
                            )
                            .get(),
                    (m, e) -> openItemLoadoutMenu(player, loadout, 1)
            );
        }

        int x = 0;
        int y = 1;
        for (int i = 0; i < 36; i++) {
            int itemNumber = ((page - 1) * 36) + i;
            if (itemNumber < itemInventory.size()) {
                AbstractItem<?, ?, ?> item = itemInventory.get(itemNumber);
                menu.setItem(x, y,
                        item.generateItemStack(),
                        (m, e) -> {
                        }
                );
                x++;
                if (x == 9) {
                    x = 0;
                    y++;
                }
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page - 1);
                        openItemMenuInternal(player, databasePlayer);
                    }
            );
        }
        if (itemInventory.size() > (page * 36)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page + 1);
                        openItemMenuInternal(player, databasePlayer);
                    }
            );
        }
        menu.setItem(1, 5,
                new ItemBuilder(Material.WORKBENCH)
                        .name(ChatColor.GREEN + "Create Loadout")
                        .get(),
                (m, e) -> {
                    if (itemsManager.getLoadouts().size() >= 9) {
                        player.sendMessage(ChatColor.RED + "You can only have up to 9 loadouts!");
                    } else {
                        SignGUI.open(player, new String[]{"", "Enter", "Loadout Name", ""}, (p, lines) -> {
                            String name = lines[0];
                            if (!name.matches("[a-zA-Z0-9 ]+")) {
                                player.sendMessage(ChatColor.RED + "Invalid name!");
                                return;
                            }
                            if (loadouts.stream().anyMatch(itemLoadout -> itemLoadout.getName().equalsIgnoreCase(name))) {
                                player.sendMessage(ChatColor.RED + "You already have a loadout with that name!");
                                return;
                            }
                            loadouts.add(new ItemLoadout(name));
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                            openItemMenuInternal(player, databasePlayer);
                        });
                    }
                }
        );
        menu.setItem(2, 5,
                new ItemBuilder(Material.SKULL_ITEM, 1, (short) SkullType.ZOMBIE.ordinal())
                        .name("§aMob Drops")
                        .lore(Arrays.stream(MobDrops.VALUES)
                                    .map(drop -> drop.getCostColoredName(databasePlayer.getPveStats()
                                                                                       .getMobDrops()
                                                                                       .getOrDefault(drop, 0L)))
                                    .collect(Collectors.joining("\n")))
                        .get(),
                (m, e) -> {}
        );
        addItemMenuSettings(player, databasePlayer, menuSettings, sortedBy, filterBy, addonFilter, menu);
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
//        if (menuSettings.isOpenedFromNPC()) {
//            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
//        } else {
//            menu.setItem(4, 5, WarlordsNewHotbarMenu.PvEMenu.MENU_BACK_PVE, (m, e) -> WarlordsNewHotbarMenu.PvEMenu.openPvEMenu(player));
//        }
        menu.openForPlayer(player);
    }

    private static void addItemMenuSettings(
            Player player,
            DatabasePlayer databasePlayer,
            PlayerItemMenuSettings menuSettings,
            SortOptions sortedBy,
            ItemTier filterBy,
            int addonFilter,
            Menu menu
    ) {
        menu.setItem(3, 5,
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(ChatColor.GREEN + "Reset Settings")
                        .lore(ChatColor.GRAY + "Reset the filter, sort, and order of weapons")
                        .get(),
                (m, e) -> {
                    menuSettings.reset();
                    openItemMenuInternal(player, databasePlayer);
                }
        );
        StringBuilder addonFilterLore = new StringBuilder();
        String[] addonFilters = PlayerItemMenuSettings.ADDON_FILTERS;
        for (int i = 0; i < addonFilters.length; i++) {
            String filter = addonFilters[i];
            addonFilterLore.append(addonFilter == i ? ChatColor.AQUA : ChatColor.GRAY).append(filter).append("\n");
        }
        menu.setItem(5, 5,
                new ItemBuilder(Material.HOPPER)
                        .name(ChatColor.GREEN + "Filter By")
                        .lore(
                                Arrays.stream(ItemTier.VALUES)
                                      .map(value -> (filterBy == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                      .collect(Collectors.joining("\n")),
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK " + ChatColor.GREEN + "to change tier filter",
                                "",
                                addonFilterLore +
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK " + ChatColor.GREEN + "to change bonus filter"
                        )
                        .get(),
                (m, e) -> {
                    if (e.isLeftClick()) {
                        menuSettings.setTierFilter(filterBy.next());
                    } else if (e.isRightClick()) {
                        menuSettings.nextAddonFilter();
                    }
                    menuSettings.setPage(1);
                    openItemMenuInternal(player, databasePlayer);
                }
        );
        menu.setItem(6, 5,
                new ItemBuilder(Material.REDSTONE_COMPARATOR)
                        .name(ChatColor.GREEN + "Sort By")
                        .lore(Arrays.stream(SortOptions.VALUES)
                                    .map(value -> (sortedBy == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                    .collect(Collectors.joining("\n"))
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setSortOption(sortedBy.next());
                    openItemMenuInternal(player, databasePlayer);
                }
        );
        menu.setItem(7, 5,
                new ItemBuilder(Material.LEVER)
                        .name(ChatColor.GREEN + "Sort Order")
                        .lore(menuSettings.isAscending() ?
                              ChatColor.AQUA + "Ascending\n" + ChatColor.GRAY + "Descending" :
                              ChatColor.GRAY + "Ascending\n" + ChatColor.AQUA + "Descending"
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setAscending(!menuSettings.isAscending());
                    openItemMenuInternal(player, databasePlayer);
                }
        );
    }

    public static void openItemLoadoutMenu(Player player, ItemLoadout itemLoadout, int page) {
        /*
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            Menu menu = new Menu("Loadout: " + itemLoadout.getName(), 9 * 6);

            ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
            List<ItemLoadout> loadouts = itemsManager.getLoadouts();
            List<ItemEntry> itemInventory = new ArrayList<>(itemsManager.getItemInventory());
            List<ItemEntry> equippedItems = itemInventory.stream()
                    .filter(itemEntry -> itemLoadout.getItems().contains(itemEntry.getUUID()))
                    .collect(Collectors.toList());
            itemInventory.removeAll(equippedItems);

            int loadoutWeight = equippedItems.stream().mapToInt(itemEntry -> itemEntry.getItem().getWeight()).sum();

            for (ItemAttribute attribute : ItemAttribute.VALUES) {
                for (int i = 0; i < attribute.maxEquipped; i++) {
                    if (i < attribute.currentEquipped) {
                        boolean equipped = false;
                        for (ItemEntry equippedItem : equippedItems) {
                            if (equippedItem.getItem().getAttribute() == attribute) {
                                menu.addItem(equippedItem.getItem().generateItemBuilder()
                                                .addLore(
                                                        "",
                                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK" + ChatColor.GREEN + " to swap this item",
                                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GREEN + " to unequip this item"
                                                )
                                                .get(),
                                        (m, e) -> {
                                            if (e.isLeftClick()) {
                                                openItemEquipMenu(player, itemLoadout, 1, attribute, equippedItem);
                                            } else if (e.isRightClick()) {
                                                itemLoadout.getItems().remove(equippedItem.getUUID());
                                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                                openItemLoadoutMenu(player, itemLoadout, page);
                                            }
                                        }
                                );
                                equipped = true;
                                equippedItems.remove(equippedItem);
                                break;
                            }
                        }
                        if (!equipped) {
                            menu.addItem(new ItemBuilder(attribute.itemStack)
                                            .name(ChatColor.GREEN + "Click to Equip Item")
                                            .get(),
                                    (m, e) -> openItemEquipMenu(player, itemLoadout, 1, attribute, null)
                            );
                        }
                    } else {
                        menu.addItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                                        .name(" ")
                                        .get(),
                                (m, e) -> {
                                }
                        );
                    }
                }
            }

            int x = 0;
            int y = 1;
            for (int i = 0; i < 36; i++) {
                int itemNumber = ((page - 1) * 36) + i;
                if (itemNumber < itemInventory.size()) {
                    ItemEntry itemEntry = itemInventory.get(itemNumber);
                    menu.setItem(x, y,
                            itemEntry.getItem().generateItemStack(),
                            (m, e) -> {

                            }
                    );
                    x++;
                    if (x == 9) {
                        x = 0;
                        y++;
                    }
                }
            }

            if (page - 1 > 0) {
                menu.setItem(0, 5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Previous Page")
                                .lore(ChatColor.YELLOW + "Page " + (page - 1))
                                .get(),
                        (m, e) -> openItemLoadoutMenu(player, itemLoadout, page - 1)
                );
            }
            if (itemInventory.size() > (page * 36)) {
                menu.setItem(8, 5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Next Page")
                                .lore(ChatColor.YELLOW + "Page " + (page + 1))
                                .get(),
                        (m, e) -> openItemLoadoutMenu(player, itemLoadout, page + 1)
                );
            }
            menu.setItem(1, 5,
                    new ItemBuilder(Material.NAME_TAG)
                            .name(ChatColor.GREEN + "Rename Loadout")
                            .get(),
                    (m, e) -> {
                        SignGUI.open(player, new String[]{"", "Enter", "Loadout Name", ""}, (p, lines) -> {
                            String name = lines[0];
                            if (!name.matches("[a-zA-Z0-9 ]+")) {
                                player.sendMessage(ChatColor.RED + "Invalid name!");
                                return;
                            }
                            if (loadouts.stream().anyMatch(loadout -> loadout.getName().equalsIgnoreCase(name))) {
                                player.sendMessage(ChatColor.RED + "You already have a loadout with that name!");
                                return;
                            }
                            itemLoadout.setName(name);
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                            openItemLoadoutMenu(player, itemLoadout, page);
                        });
                    }
            );

            menu.setItem(2, 5,
                    new ItemBuilder(Material.LAVA_BUCKET)
                            .name(ChatColor.RED + "Delete Loadout")
                            .get(),
                    (m, e) -> {
                        if (itemLoadout.getName().equals("Default")) {
                            player.sendMessage(ChatColor.RED + "You cannot delete the default loadout!");
                            return;
                        }
                        Menu.openConfirmationMenu(
                                player,
                                "Delete Loadout",
                                3,
                                Arrays.asList(
                                        ChatColor.GRAY + "Delete Loadout: " + ChatColor.GOLD + itemLoadout.getName(),
                                        "",
                                        ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This cannot be undone!"
                                ),
                                Collections.singletonList(ChatColor.GRAY + "Go back"),
                                (m2, e2) -> {
                                    loadouts.remove(itemLoadout);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openItemMenu(player, 1);
                                },
                                (m2, e2) -> openItemLoadoutMenu(player, itemLoadout, page),
                                (m2) -> {
                                }
                        );
                    }
            );

            menu.setItem(3, 5,
                    new ItemBuilder(Material.SIGN)
                            .name(ChatColor.GREEN + "Loadout Weight: " + ChatColor.GOLD + loadoutWeight)
                            .lore(ChatColor.GREEN + "Max Weights: ")
                            .addLore(Arrays.stream(Specializations.VALUES)
                                    .map(spec -> ChatColor.DARK_GRAY + " - " + (spec == databasePlayer.getLastSpec() ? ChatColor.GREEN : ChatColor.GRAY) +
                                            spec.name + ": " + ChatColor.GOLD + ItemsManager.getMaxWeight(databasePlayer, spec))
                                    .collect(Collectors.joining("\n")))
                            .get(),
                    (m, e) -> {

                    }
            );
            menu.setItem(4, 5, MENU_BACK, (m, e) -> openItemMenu(player, 1));
            List<String> lore = new ArrayList<>();
            for (int i = 0; i < loadouts.size(); i++) {
                lore.add("" + (loadouts.get(i).equals(itemLoadout) ? ChatColor.GREEN : ChatColor.GRAY) + (i + 1) + ". " + loadouts.get(i).getName());
            }
            menu.setItem(5, 5,
                    new ItemBuilder(Material.TRIPWIRE_HOOK)
                            .name(ChatColor.GREEN + "Change Loadout Priority")
                            .lore(lore)
                            .get(),
                    (m, e) -> {
                        int loadoutIndex = loadouts.indexOf(itemLoadout);
                        int newLoadoutIndex;
                        if (loadoutIndex == loadouts.size() - 1) {
                            newLoadoutIndex = 0;
                        } else {
                            newLoadoutIndex = loadoutIndex + 1;
                        }
                        loadouts.remove(itemLoadout);
                        loadouts.add(newLoadoutIndex, itemLoadout);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        openItemLoadoutMenu(player, itemLoadout, page);
                    }
            );
            lore.clear();
            lore.add((itemLoadout.getDifficulty() == null ? ChatColor.GREEN : ChatColor.GRAY) + "Any");
            DifficultyIndex[] difficulties = DifficultyIndex.VALUES;
            for (DifficultyIndex value : difficulties) {
                lore.add((itemLoadout.getDifficulty() == value ? ChatColor.GREEN : ChatColor.GRAY) + value.getName());
            }
            menu.setItem(6, 5,
                    new ItemBuilder(Material.REDSTONE_COMPARATOR)
                            .name(ChatColor.GREEN + "Bind to Difficulty")
                            .lore(lore)
                            .get(),
                    (m, e) -> {
                        if (itemLoadout.getDifficulty() == null) {
                            itemLoadout.setDifficulty(DifficultyIndex.VALUES[0]);
                        } else if (itemLoadout.getDifficulty().ordinal() == DifficultyIndex.VALUES.length - 1) {
                            itemLoadout.setDifficulty(null);
                        } else {
                            itemLoadout.setDifficulty(itemLoadout.getDifficulty().next());
                        }
                        openItemLoadoutMenu(player, itemLoadout, page);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    }
            );
            lore.clear();
            lore.add((itemLoadout.getSpec() == null ? ChatColor.GREEN : ChatColor.GRAY) + "Any");
            Specializations[] specializations = Specializations.VALUES;
            for (Specializations value : specializations) {
                lore.add((itemLoadout.getSpec() == value ? ChatColor.GREEN : ChatColor.GRAY) + value.name);
            }
            menu.setItem(7, 5,
                    new ItemBuilder(Material.SLIME_BALL)
                            .name(ChatColor.GREEN + "Bind to Specialization")
                            .lore(lore)
                            .get(),
                    (m, e) -> {
                        if (itemLoadout.getSpec() == null) {
                            itemLoadout.setSpec(Specializations.VALUES[0]);
                        } else if (itemLoadout.getSpec().ordinal() == Specializations.VALUES.length - 1) {
                            itemLoadout.setSpec(null);
                        } else {
                            itemLoadout.setSpec(itemLoadout.getSpec().next());
                        }
                        openItemLoadoutMenu(player, itemLoadout, page);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    }
            );
            menu.openForPlayer(player);
        });

         */
    }

    public static void openMichaelItemMenu(Player player, DatabasePlayer databasePlayer) {
        Menu menu = new Menu("Michael", 9 * 6);

        menu.setItem(4, 0,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Your Blessings")
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(1, 1,
                new ItemBuilder(Material.PAPER)
                        .name(ChatColor.GREEN + "Buy a Blessing")
                        .get(),
                (m, e) -> {
                }
        );
        menu.setItem(4, 1,
                new ItemBuilder(Material.ANVIL)
                        .name(ChatColor.GREEN + "Apply a Blessing")
                        .get(),
                (m, e) -> {

                }
        );
        menu.setItem(7, 1,
                new ItemBuilder(Material.EMPTY_MAP)
                        .name(ChatColor.GREEN + "Remove a Curse")
                        .get(),
                (m, e) -> {
                }
        );


        menu.openForPlayer(player);
    }


    public static void openApplyBlessingMenu(Player player, DatabasePlayer databasePlayer, ApplyBlessingMenuData menuData) {
        AbstractItem<?, ?, ?> item = menuData.getItem();
        Integer blessing = menuData.getBlessing();
        ItemStack selectedItem;
        ItemStack selectedBlessing;
        if (item != null) {
            selectedItem = new ItemBuilder(item.generateItemStack())
                    .addLore(
                            "",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a different item"
                    )
                    .get();
            if (blessing != null) {
                ItemModifier<?> itemBlessing = item.getBlessings()[blessing];
                selectedBlessing = new ItemBuilder(Material.PAPER)
                        .name(itemBlessing.getName())
                        .addLore(
                                ChatColor.GREEN + itemBlessing.getDescription(),
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a different blessing"
                        )
                        .get();
            } else {
                selectedBlessing = new ItemBuilder(Material.PAPER)
                        .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a blessing")
                        .get();
            }
        } else {
            selectedItem = new ItemBuilder(Material.SKULL_ITEM)
                    .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select an item")
                    .get();
            selectedBlessing = new ItemBuilder(Material.EMPTY_MAP)
                    .name(ChatColor.RED + "Select an item first")
                    .get();
        }

        Menu menu = new Menu("Apply a Blessing", 9 * 6);
        menu.setItem(1, 1,
                selectedItem,
                (m, e) -> {
                    openApplyBlessingItemSelectMenu(player, databasePlayer, new PlayerItemMenuSettings(), menuData);
                }
        );
        menu.setItem(4, 1,
                selectedBlessing,
                (m, e) -> {
                    openApplyBlessingBlessingSelectMenu(player, databasePlayer, menuData);
                }
        );
        //TODO APPLY ITEM
        menu.openForPlayer(player);
    }

    private static void openApplyBlessingItemSelectMenu(
            Player player,
            DatabasePlayer databasePlayer,
            PlayerItemMenuSettings menuSettings,
            ApplyBlessingMenuData menuData
    ) {
        int page = menuSettings.getPage();
        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
        menuSettings.sort(playerSettings.getSelectedSpec(), Specializations.getClass(playerSettings.getSelectedSpec()));
        List<AbstractItem<?, ?, ?>> itemInventory = new ArrayList<>(menuSettings.getSortedItemInventory());

        Menu menu = new Menu("Select an Item", 9 * 5);

        SortOptions sortedBy = menuSettings.getSortOption();
        ItemTier filterBy = menuSettings.getTierFilter();
        int addonFilter = menuSettings.getAddonFilter();

        int x = 0;
        int y = 1;
        for (int i = 0; i < 45; i++) {
            int itemNumber = ((page - 1) * 45) + i;
            if (itemNumber < itemInventory.size()) {
                AbstractItem<?, ?, ?> item = itemInventory.get(itemNumber);
                menu.setItem(x, y,
                        new ItemBuilder(item.generateItemStack())
                                .addLore(
                                        "",
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select"
                                )
                                .get(),
                        (m, e) -> {
                            menuData.setItem(item);
                            openApplyBlessingMenu(player, databasePlayer, menuData);
                        }
                );
                x++;
                if (x == 9) {
                    x = 0;
                    y++;
                }
            }
        }
        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page - 1);
                        openApplyBlessingItemSelectMenu(player, databasePlayer, menuSettings, menuData);
                    }
            );
        }
        if (itemInventory.size() > (page * 45)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page + 1);
                        openApplyBlessingItemSelectMenu(player, databasePlayer, menuSettings, menuData);
                    }
            );
        }

        addItemMenuSettings(player, databasePlayer, menuSettings, sortedBy, filterBy, addonFilter, menu);

        menu.setItem(4, 5,
                Menu.MENU_BACK,
                (m, e) -> {
                    openApplyBlessingMenu(player, databasePlayer, menuData);
                }
        );
    }

    private static void openApplyBlessingBlessingSelectMenu(
            Player player,
            DatabasePlayer databasePlayer,
            ApplyBlessingMenuData menuData
    ) {
        Menu menu = new Menu("Select a Blessing Tier", 9 * 4);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Integer blessingAmount = databasePlayer.getPveStats().getItemsManager().getBlessingAmount(i + 1);
            menu.setItem(i + 2, 1,
                    new ItemBuilder(Material.PAPER)
                            .name(ChatColor.GREEN + "Tier " + (i + 1))
                            .lore(
                                    ChatColor.GRAY + "Amount: " + blessingAmount,
                                    "",
                                    ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select"
                            )
                            .get(),
                    (m, e) -> {
                        if (true || blessingAmount > 0) {
                            menuData.setBlessing(finalI + 1);
                            openApplyBlessingMenu(player, databasePlayer, menuData);
                        }
                    }
            );
        }
        menu.setItem(4, 3,
                Menu.MENU_BACK,
                (m, e) -> {
                    openApplyBlessingMenu(player, databasePlayer, menuData);
                }
        );
    }


    public enum SortOptions {
        DATE("Date", Comparator.comparing(AbstractItem::getObtainedDate)),
        TIER("Tier", Comparator.comparing(AbstractItem::getTier)),
        ITEM_SCORE("Item Score", Comparator.comparing(AbstractItem::getItemScore));

        private static final SortOptions[] VALUES = values();
        public final String name;
        public final Comparator<AbstractItem<?, ?, ?>> comparator;

        SortOptions(String name, Comparator<AbstractItem<?, ?, ?>> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        public SortOptions next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }


    private static class ApplyBlessingMenuData {
        private AbstractItem<?, ?, ?> item;
        private Integer blessing;

        public ApplyBlessingMenuData(AbstractItem<?, ?, ?> item, Integer blessing) {
            this.item = item;
            this.blessing = blessing;
        }

        public AbstractItem<?, ?, ?> getItem() {
            return item;
        }

        public void setItem(AbstractItem<?, ?, ?> item) {
            this.item = item;
        }

        public Integer getBlessing() {
            return blessing;
        }

        public void setBlessing(Integer blessing) {
            this.blessing = blessing;
        }
    }


    static class PlayerItemMenuSettings {
        public static final String[] ADDON_FILTERS = new String[]{"None", "Selected Spec", "Selected Class"};
        private boolean openedFromNPC = false;
        private int page = 1;
        private List<AbstractItem<?, ?, ?>> itemInventory = new ArrayList<>();
        private List<AbstractItem<?, ?, ?>> sortedItemInventory = new ArrayList<>();
        private ItemTier tierFilter = ItemTier.ALL;
        private int addonFilter = 0; // 0 = none, 1 = spec, 2 = class
        private SortOptions sortOption = SortOptions.DATE;
        private boolean ascending = true; //ascending = smallest -> largest/recent

        public void reset() {
            this.page = 1;
            this.tierFilter = ItemTier.ALL;
            this.addonFilter = 0;
            this.sortOption = SortOptions.DATE;
            this.ascending = true;
        }

        public void sort(Specializations selectedSpec, Classes selectedClas) {
            sortedItemInventory = new ArrayList<>(itemInventory);
            if (tierFilter != ItemTier.ALL) {
                sortedItemInventory.removeIf(item -> item.getTier() != tierFilter);
            }
            if (addonFilter != 0) {
                if (addonFilter == 1) {
                    sortedItemInventory.removeIf(item -> !(item instanceof ItemAddonSpecBonus && ((ItemAddonSpecBonus) item).getSpec() == selectedSpec));
                } else if (addonFilter == 2) {
                    sortedItemInventory.removeIf(item -> !(item instanceof ItemAddonClassBonus && ((ItemAddonClassBonus) item).getClasses() == selectedClas));
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

        public List<AbstractItem<?, ?, ?>> getSortedItemInventory() {
            return sortedItemInventory;
        }

        public void setItemInventory(List<AbstractItem<?, ?, ?>> itemInventory) {
            this.itemInventory = itemInventory;
            this.sortedItemInventory = new ArrayList<>(itemInventory);
        }

        public ItemTier getTierFilter() {
            return tierFilter;
        }

        public void setTierFilter(ItemTier tierFilter) {
            this.tierFilter = tierFilter;
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

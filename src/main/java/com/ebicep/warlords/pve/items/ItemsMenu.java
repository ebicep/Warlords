package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.items.pojos.ItemEntry;
import com.ebicep.warlords.database.repositories.player.pojos.pve.ItemLoadout;
import com.ebicep.warlords.database.repositories.player.pojos.pve.ItemsManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;

public class ItemsMenu {

    public static void openItemMenu(Player player, int page) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            Menu menu = new Menu("Items", 9 * 6);

            ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
            List<ItemEntry> itemInventory = new ArrayList<>(itemsManager.getItemInventory());

            List<ItemLoadout> loadouts = itemsManager.getLoadouts();
            for (int i = 0, loadoutsSize = loadouts.size(); i < loadoutsSize; i++) {
                ItemLoadout loadout = loadouts.get(i);
                List<ItemEntry> equippedItems = itemInventory.stream()
                        .filter(itemEntry -> loadout.getItems().contains(itemEntry.getUUID()))
                        .collect(Collectors.toList());
                menu.addItem(new ItemBuilder(loadout.getName().equals("Default") ? Material.IRON_DOOR : Material.WOOD_DOOR)
                                .name(ChatColor.GREEN + "Loadout #" + (i + 1) + ": " + ChatColor.GOLD + loadout.getName())
                                .lore(
                                        ChatColor.GRAY + "Weight: " + ChatColor.GOLD + equippedItems.stream()
                                                .mapToInt(itemEntry -> itemEntry.getItem().getWeight())
                                                .sum(),
                                        ChatColor.GRAY + "Difficulty: " + ChatColor.GOLD + (loadout.getDifficulty() == null ? "All" : loadout.getDifficulty()
                                                .getName()),
                                        ChatColor.GRAY + "Specialization: " + ChatColor.GOLD + (loadout.getSpec() == null ? "All" : loadout.getSpec().name)
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
                        (m, e) -> openItemMenu(player, page - 1)
                );
            }
            if (itemInventory.size() > (page * 36)) {
                menu.setItem(8, 5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Next Page")
                                .lore(ChatColor.YELLOW + "Page " + (page + 1))
                                .get(),
                        (m, e) -> openItemMenu(player, page + 1)
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
                                openItemMenu(player, page);
                            });
                        }
                    }
            );
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

    public static void openItemLoadoutMenu(Player player, ItemLoadout itemLoadout, int page) {
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
    }

    public static void openItemEquipMenu(Player player, ItemLoadout itemLoadout, int page, ItemAttribute attribute, ItemEntry previousEntry) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            Menu menu = new Menu(attribute.name + " Items", 9 * 6);

            List<ItemEntry> inventory = databasePlayer.getPveStats().getItemsManager().getItemInventory();
            Set<ItemFamily> families = new HashSet<>();
            for (ItemEntry itemEntry : inventory) {
                if (itemLoadout.getItems().contains(itemEntry.getUUID())) {
                    families.add(itemEntry.getItem().getFamily());
                }
            }
            List<ItemEntry> itemInventory = inventory
                    .stream()
                    .filter(itemEntry -> itemEntry.getItem().getAttribute() == attribute)
                    .filter(itemEntry -> !itemLoadout.getItems().contains(itemEntry.getUUID()))
                    .filter(itemEntry -> !families.contains(itemEntry.getItem().getFamily()))
                    .collect(Collectors.toList());

            int x = 0;
            int y = 0;
            for (int i = 0; i < 45; i++) {
                int itemNumber = ((page - 1) * 45) + i;
                if (itemNumber < itemInventory.size()) {
                    ItemEntry itemEntry = itemInventory.get(itemNumber);
                    menu.setItem(x, y,
                            itemEntry.getItem().generateItemStack(),
                            (m, e) -> {
                                if (previousEntry != null) {
                                    itemLoadout.getItems().remove(previousEntry.getUUID());
                                }
                                itemLoadout.getItems().add(itemEntry.getUUID());
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                openItemLoadoutMenu(player, itemLoadout, 1);
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
            if (itemInventory.size() > (page * 45)) {
                menu.setItem(8, 5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Next Page")
                                .lore(ChatColor.YELLOW + "Page " + (page + 1))
                                .get(),
                        (m, e) -> openItemLoadoutMenu(player, itemLoadout, page + 1)
                );
            }

            menu.setItem(3, 5, MENU_BACK, (m, e) -> openItemLoadoutMenu(player, itemLoadout, 1));
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

}

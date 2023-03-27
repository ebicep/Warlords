package com.ebicep.warlords.pve.items.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.items.ItemLoadout;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.menu.util.ItemSearchMenu;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class ItemEquipMenu {

    public static final HashMap<UUID, ItemSearchMenu.PlayerItemMenuSettings> PLAYER_MENU_SETTINGS = new HashMap<>();

    public static void openItemEquipMenuExternal(Player player, boolean fromNPC) {
        UUID uuid = player.getUniqueId();
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
            List<AbstractItem<?, ?, ?>> itemInventory = new ArrayList<>(itemsManager.getItemInventory());

            PLAYER_MENU_SETTINGS.putIfAbsent(uuid, new ItemSearchMenu.PlayerItemMenuSettings(databasePlayer));
            ItemSearchMenu.PlayerItemMenuSettings menuSettings = PLAYER_MENU_SETTINGS.get(uuid);
            menuSettings.setOpenedFromNPC(fromNPC);
            menuSettings.setItemInventory(itemInventory);
            menuSettings.sort();

            openItemEquipMenuInternal(player, databasePlayer);
        });

    }

    public static void openItemEquipMenuInternal(Player player, DatabasePlayer databasePlayer) {
        UUID uuid = player.getUniqueId();
        PLAYER_MENU_SETTINGS.putIfAbsent(uuid, new ItemSearchMenu.PlayerItemMenuSettings(databasePlayer));
        ItemSearchMenu.PlayerItemMenuSettings menuSettings = PLAYER_MENU_SETTINGS.get(uuid);

        ItemSearchMenu menu = new ItemSearchMenu(
                player, "Items",
                (i, m, e) -> {},
                itemBuilder -> itemBuilder,
                menuSettings,
                databasePlayer
        );

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
//        if (menuSettings.isOpenedFromNPC()) {
//            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
//        } else {
//            menu.setItem(4, 5, WarlordsNewHotbarMenu.PvEMenu.MENU_BACK_PVE, (m, e) -> WarlordsNewHotbarMenu.PvEMenu.openPvEMenu(player));
//        }
        menu.open();
    }

    public static void openItemLoadoutMenu(Player player, ItemLoadout possibleLoadout, DatabasePlayer databasePlayer) {
        AtomicReference<ItemLoadout> atomicItemLoadout = new AtomicReference<>(possibleLoadout);
        if (atomicItemLoadout.get() == null) {
            atomicItemLoadout.set(databasePlayer.getPveStats().getItemsManager().getLoadouts().get(0));
        }
        ItemLoadout itemLoadout = atomicItemLoadout.get();
        Menu menu = new Menu("Loadout: " + itemLoadout.getName(), 9 * 6);

        ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
        List<ItemLoadout> loadouts = itemsManager.getLoadouts();
        List<AbstractItem<?, ?, ?>> itemInventory = new ArrayList<>(itemsManager.getItemInventory());
        List<AbstractItem<?, ?, ?>> equippedItems = itemInventory.stream()
                                                                 .filter(itemEntry -> itemLoadout.getItems().contains(itemEntry.getUUID()))
                                                                 .collect(Collectors.toList());
        itemInventory.removeAll(equippedItems);

        int loadoutWeight = equippedItems.stream().mapToInt(AbstractItem::getWeight).sum();
        addWeightPercentageBar(player, databasePlayer, itemLoadout, menu, loadoutWeight);

        int x = 0;
        int y = 2;
        for (ItemTier tier : ItemTier.VALUES) {
            for (int j = 0; j < tier.maxEquipped; j++) {
                menu.setItem(x, y,
                        new ItemBuilder(tier.clayBlock)
                                .name(tier.getColoredName() + " Item")
                                .get(),
                        (m, e) -> {
                        }
                );
                boolean equipped = false;
                for (AbstractItem<?, ?, ?> item : equippedItems) {
                    if (item.getTier() != tier) {
                        continue;
                    }
                    menu.setItem(x, y + 1,
                            item.generateItemBuilder()
                                .addLore(
                                        "",
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK" + ChatColor.GREEN + " to swap this item",
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GREEN + " to unequip this item"
                                )
                                .get(),
                            (m, e) -> {
                                if (e.isLeftClick()) {
                                    openItemEquipMenu(player, databasePlayer, itemLoadout, tier, item);
                                } else if (e.isRightClick()) {
                                    itemLoadout.getItems().remove(item.getUUID());
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openItemLoadoutMenu(player, itemLoadout, databasePlayer);
                                }
                            }
                    );
                    equipped = true;
                    equippedItems.remove(item);
                    break;
                }
                if (!equipped) {
                    menu.setItem(x, y + 1,
                            new ItemBuilder(ItemTier.ALL.clayBlock)
                                    .name(ChatColor.GREEN + "Click to Equip Item")
                                    .get(),
                            (m, e) -> openItemEquipMenu(player, databasePlayer, itemLoadout, tier, null)
                    );
                }
                x++;
            }
            if (x == 9) {
                x = 0;
                y++;
            }
        }

        List<String> lore = new ArrayList<>();
        List<ItemLoadout> sortedLoadouts = loadouts.stream()
                                                   .sorted(Comparator.comparing(ItemLoadout::getCreationDate))
                                                   .collect(Collectors.toList());
        for (int i = 0; i < sortedLoadouts.size(); i++) {
            ItemLoadout l = sortedLoadouts.get(i);
            DifficultyIndex difficulty = l.getDifficulty();
            Specializations spec = l.getSpec();
            lore.add((l.equals(itemLoadout) ? ChatColor.AQUA : ChatColor.GRAY).toString() + i + ". " + l.getName() +
                    " (" + l.getWeight(itemsManager) + " | " + (difficulty == null ? "Any" : difficulty.getName()) + " | " + (spec == null ? "Any" : spec.name) + ")");
        }
        menu.setItem(0, 5,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Change Loadout (Weight | Difficulty | Spec)")
                        .lore(lore)
                        .get(),
                (m, e) -> {
                    int index = sortedLoadouts.indexOf(itemLoadout);
                    int nextLoadout = index >= sortedLoadouts.size() - 1 ? 0 : index + 1;
                    openItemLoadoutMenu(player, sortedLoadouts.get(nextLoadout), databasePlayer);
                }
        );
        menu.setItem(1, 5,
                new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.GREEN + "Create Loadout")
                        .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "Create a new loadout to customize your experience.", 150))
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
                            if (loadouts.stream().anyMatch(i -> i.getName().equalsIgnoreCase(name))) {
                                player.sendMessage(ChatColor.RED + "You already have a loadout with that name!");
                                return;
                            }
                            ItemLoadout newLoadout = new ItemLoadout(name);
                            loadouts.add(newLoadout);
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                            openItemLoadoutMenu(player, newLoadout, databasePlayer);
                        });
                    }
                }
        );
        menu.setItem(2, 5,
                new ItemBuilder(Material.NAME_TAG)
                        .name(ChatColor.GREEN + "Rename Loadout")
                        .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "Rename the current loadout", 150))
                        .get(),
                (m, e) -> {
                    if (itemLoadout.getName().equals("Default")) {
                        player.sendMessage(ChatColor.RED + "You cannot rename the default loadout!");
                        return;
                    }
                    SignGUI.open(player, new String[]{"", "Enter", "Loadout Name", ""}, (p, lines) -> {
                        String name = lines[0];
                        if (!name.matches("[a-zA-Z0-9 ]+")) {
                            player.sendMessage(ChatColor.RED + "Invalid name!");
                            return;
                        }
                        if (loadouts.stream().anyMatch(l -> l.getName().equalsIgnoreCase(name))) {
                            player.sendMessage(ChatColor.RED + "You already have a loadout with that name!");
                            return;
                        }
                        itemLoadout.setName(name);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        openItemLoadoutMenu(player, itemLoadout, databasePlayer);
                    });
                }
        );

        menu.setItem(3, 5,
                new ItemBuilder(Material.LAVA_BUCKET)
                        .name(ChatColor.RED + "Delete Loadout")
                        .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "Delete the current loadout", 150))
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
                                openItemLoadoutMenu(player, null, databasePlayer);
                            },
                            (m2, e2) -> openItemLoadoutMenu(player, itemLoadout, databasePlayer),
                            (m2) -> {
                            }
                    );
                }
        );

        lore.clear();
        for (int i = 0; i < loadouts.size(); i++) {
            lore.add("" + (loadouts.get(i).equals(itemLoadout) ? ChatColor.AQUA : ChatColor.GRAY) + (i + 1) + ". " + loadouts.get(i).getName());
        }
        menu.setItem(5, 5,
                new ItemBuilder(Material.TRIPWIRE_HOOK)
                        .name(ChatColor.GREEN + "Change Loadout Priority")
                        .lore(
                                WordWrap.wrapWithNewline(ChatColor.GRAY + "Change the priority of the current loadout, for when you have " +
                                                "multiple loadouts with the same filters.",
                                        170
                                ),
                                ""
                        )
                        .addLore(lore)
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
                    openItemLoadoutMenu(player, itemLoadout, databasePlayer);
                }
        );
        lore.clear();
        lore.add((itemLoadout.getDifficulty() == null ? ChatColor.AQUA : ChatColor.GRAY) + "Any");
        DifficultyIndex[] difficulties = DifficultyIndex.VALUES;
        for (DifficultyIndex value : difficulties) {
            lore.add((itemLoadout.getDifficulty() == value ? ChatColor.AQUA : ChatColor.GRAY) + value.getName());
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
                    openItemLoadoutMenu(player, itemLoadout, databasePlayer);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
        );
        lore.clear();
        lore.add((itemLoadout.getSpec() == null ? ChatColor.AQUA : ChatColor.GRAY) + "Any");
        Specializations[] specializations = Specializations.VALUES;
        for (Specializations spec : specializations) {
            lore.add((itemLoadout.getSpec() == spec ? ChatColor.AQUA : ChatColor.GRAY) + spec.name + " - " + ItemsManager.getMaxWeight(databasePlayer, spec));
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
                    openItemLoadoutMenu(player, itemLoadout, databasePlayer);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                }
        );
        menu.openForPlayer(player);
    }

    private static void addWeightPercentageBar(Player player, DatabasePlayer databasePlayer, ItemLoadout itemLoadout, Menu menu, int loadoutWeight) {
        int maxWeight = ItemsManager.getMaxWeight(databasePlayer, itemLoadout.getSpec() != null ? itemLoadout.getSpec() : databasePlayer.getLastSpec());
        double ratio = loadoutWeight * 8d / maxWeight;
        for (int i = 0; i < 8; i++) {
            ItemStack glassPane;
            if (i <= ratio - 1) {
                glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
            } else if (i != 0 && i <= ratio - .5) {
                glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
            } else {
                glassPane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
            }
            menu.setItem(i + 1, 0,
                    new ItemBuilder(glassPane)
                            .name(" ")
                            .get(),
                    (m, e) -> {

                    }
            );
        }
        menu.setItem(0, 0,
                new ItemBuilder(HeadUtils.getHead(player))
                        .name(ChatColor.GRAY + "Max Weight: " + ChatColor.GREEN + maxWeight)
                        .lore(ChatColor.GRAY + "Current Weight: " + ChatColor.GREEN + loadoutWeight)
                        .get(),
                (m, e) -> {

                }
        );
    }

    public static void openItemEquipMenu(
            Player player,
            DatabasePlayer databasePlayer,
            ItemLoadout itemLoadout,
            ItemTier tier,
            AbstractItem<?, ?, ?> previousItem
    ) {
        ItemSearchMenu menu = new ItemSearchMenu(
                player, tier.name + " Items",
                (i, m, e) -> {
                    if (previousItem != null) {
                        itemLoadout.getItems().remove(previousItem.getUUID());
                    }
                    itemLoadout.getItems().add(i.getUUID());
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    openItemLoadoutMenu(player, itemLoadout, databasePlayer);
                },
                itemBuilder -> itemBuilder,
                new ItemSearchMenu.PlayerItemMenuSettings(itemLoadout.getSpec() != null ? itemLoadout.getSpec() : databasePlayer.getLastSpec())
                        .setItemInventory(databasePlayer.getPveStats()
                                                        .getItemsManager()
                                                        .getItemInventory()
                                                        .stream()
                                                        .filter(item -> item.getTier() == tier && !itemLoadout.getItems().contains(item.getUUID()))
                                                        .collect(Collectors.toList())),
                databasePlayer,
                m -> m.setItem(4, 5,
                        Menu.MENU_BACK,
                        (m2, e2) -> openItemLoadoutMenu(player, itemLoadout, databasePlayer)
                )
        );
        menu.open();
    }

}

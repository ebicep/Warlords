package com.ebicep.warlords.pve.items.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.DifficultyMode;
import com.ebicep.warlords.pve.items.ItemLoadout;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.menu.util.ItemMenuUtil;
import com.ebicep.warlords.pve.items.menu.util.ItemSearchMenu;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.Utils;
import de.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class ItemEquipMenu {

    public static final HashMap<UUID, ItemSearchMenu.PlayerItemMenuSettings> PLAYER_MENU_SETTINGS = new HashMap<>();
    private static final ItemStack ITEM_EQUIP_MENU = new ItemBuilder(Material.ARMOR_STAND)
            .name(Component.text("Item Equip Menu", NamedTextColor.GREEN))
            .loreLEGACY(ChatColor.GRAY + "Click to customize your Item Loadouts")
            .get();

    public static void openItemEquipMenuExternal(Player player, DatabasePlayer databasePlayer) {
        UUID uuid = player.getUniqueId();
        ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
        List<AbstractItem> itemInventory = new ArrayList<>(itemsManager.getItemInventory());

        PLAYER_MENU_SETTINGS.putIfAbsent(uuid, new ItemSearchMenu.PlayerItemMenuSettings(databasePlayer));
        ItemSearchMenu.PlayerItemMenuSettings menuSettings = PLAYER_MENU_SETTINGS.get(uuid);
        menuSettings.setItemInventory(itemInventory);
        menuSettings.sort();

        openItemEquipMenuInternal(player, databasePlayer);
    }

    public static void openItemEquipMenuInternal(Player player, DatabasePlayer databasePlayer) {
        UUID uuid = player.getUniqueId();
        PLAYER_MENU_SETTINGS.putIfAbsent(uuid, new ItemSearchMenu.PlayerItemMenuSettings(databasePlayer));
        ItemSearchMenu.PlayerItemMenuSettings menuSettings = PLAYER_MENU_SETTINGS.get(uuid);

        List<UUID> equippedItems = databasePlayer.getPveStats()
                                                 .getItemsManager()
                                                 .getLoadouts()
                                                 .stream()
                                                 .map(ItemLoadout::getItems)
                                                 .flatMap(Collection::stream)
                                                 .toList();
        ItemSearchMenu menu = new ItemSearchMenu(
                player, "Items",
                (i, m, e) -> {
                    if (e.isRightClick()) {
                        i.setFavorite(!i.isFavorite());
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        AbstractItem.sendItemMessage(player, Component.text(ChatColor.GRAY + "You " + (i.isFavorite() ? "favorited" : "unfavorited") + " ")
                                                                      .hoverEvent(i.getHoverComponent())
                        );
                        openItemEquipMenuExternal(player, databasePlayer);
                        return;
                    }
                    if (i.isFavorite()) {
                        player.sendMessage(Component.text("You cannot scrap a favorited item!", NamedTextColor.RED));
                        return;
                    }
                    if (equippedItems.contains(i.getUUID())) {
                        player.sendMessage(Component.text("You cannot scrap an equipped item!", NamedTextColor.RED));
                        return;
                    }
                    Pair<Integer, Integer> scrapValue = i.getTier().scrapValue;
                    Menu.openConfirmationMenu0(player,
                            "Confirm Scrap",
                            3,
                            new ArrayList<>() {{
                                add(Component.text("Scrap this item and claim its materials.", NamedTextColor.GRAY));
                                add(Component.empty());
                                add(Component.textOfChildren(
                                        Component.text("Rewards: ", NamedTextColor.GREEN),
                                        Component.text(scrapValue.getA() + "-" + scrapValue.getB() + " Scrap Metal.", NamedTextColor.GRAY)
                                ));
                                add(Component.empty());
                                add(Component.textOfChildren(
                                        Component.text("WARNING: ", NamedTextColor.RED),
                                        Component.text("This action cannot be undone.", NamedTextColor.GRAY)
                                ));
                            }},
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                ItemsManager itemsManager = pveStats.getItemsManager();
                                int scrapAmount = Utils.generateRandomValueBetweenInclusive(scrapValue.getA(), scrapValue.getB());
                                pveStats.addCurrency(Currencies.SCRAP_METAL, scrapAmount);
                                itemsManager.removeItem(i);
                                itemsManager.getLoadouts().forEach(itemLoadout -> itemLoadout.getItems().removeIf(itemUUID -> itemUUID.equals(i.getUUID())));
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                AbstractItem.sendItemMessage(player,
                                        Component.text(ChatColor.GRAY + "You received " + scrapAmount + " Scrap Metal from scrapping ")
                                                 .hoverEvent(i.getHoverComponent())
                                );
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);

                                openItemEquipMenuExternal(player, databasePlayer);
                            },
                            (m2, e2) -> openItemEquipMenuExternal(player, databasePlayer),
                            (m2) -> {
                            }
                    );
                },
                itemBuilder -> itemBuilder
                        .addLore(
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK " + ChatColor.GREEN + "to scrap",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK " + ChatColor.GREEN + "to favorite"
                        ),
                menuSettings,
                databasePlayer,
                m -> {
                    m.setItem(1, 5, ITEM_EQUIP_MENU, (m2, e) -> ItemEquipMenu.openItemLoadoutMenu(player, null, databasePlayer));
                    m.setItem(4, 5, WarlordsNewHotbarMenu.PvEMenu.MENU_BACK_PVE, (m2, e) -> WarlordsNewHotbarMenu.PvEMenu.openPvEMenu(player));
                }
        );

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
        List<AbstractItem> equippedItems = itemLoadout.getActualItems(itemsManager);

        Specializations selectedSpec = itemLoadout.getSpec() != null ? itemLoadout.getSpec() : databasePlayer.getLastSpec();
        List<Pair<String, Integer>> weightBreakdown = ItemsManager.getMaxWeightBreakdown(databasePlayer, selectedSpec);
        int maxWeight = weightBreakdown.stream().mapToInt(Pair::getB).sum();
        int loadoutWeight = itemLoadout.getWeight(itemsManager);
        addWeightPercentageBar(menu, maxWeight, loadoutWeight);
        menu.setItem(2, 1,
                new ItemBuilder(HeadUtils.getHead(player))
                        .name(Component.text("Stat Bonuses", NamedTextColor.AQUA))
                        .loreLEGACY(ItemMenuUtil.getTotalBonusLore(equippedItems, true))
                        .get(),
                (m, e) -> {}
        );
        menu.setItem(6, 1,
                new ItemBuilder(Material.ANVIL)
                        .name(Component.text("Weight: ", NamedTextColor.GOLD)
                                       .append(Component.text(loadoutWeight, (loadoutWeight <= maxWeight ? NamedTextColor.GREEN : NamedTextColor.RED)))
                                       .append(Component.text(" / ", NamedTextColor.GRAY))
                                       .append(Component.text(maxWeight, NamedTextColor.GREEN))
                        )
                        .loreLEGACY("",
                                ChatColor.AQUA + "Breakdown (" + selectedSpec.name + "):"
                        )
                        .addLore(weightBreakdown.stream()
                                                .map(pair -> ChatColor.AQUA + "- " + ChatColor.GRAY + pair.getA() + ": " + ChatColor.GREEN + pair.getB())
                                                .collect(Collectors.toList())
                        )
                        .get(),
                (m, e) -> {}
        );
        int x = 0;
        int y = 2;
        for (ItemTier tier : ItemTier.VALID_VALUES) {
            for (int j = 0; j < tier.maxEquipped; j++) {
                menu.setItem(x, y,
                        new ItemBuilder(tier.clayBlock)
                                .name(tier.getColoredName() + " Item")
                                .get(),
                        (m, e) -> {
                        }
                );
                boolean equipped = false;
                for (AbstractItem item : equippedItems) {
                    if (item.getTier() != tier) {
                        continue;
                    }
                    menu.setItem(x, y + 1,
                            item.generateItemBuilder()
                                .addLore(
                                        "",
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK" +
                                                ChatColor.GREEN + " to swap this item.",
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" +
                                                ChatColor.GREEN + " to unequip this item."
                                )
                                .get(),
                            (m, e) -> {
                                if (e.isLeftClick()) {
                                    openItemEquipMenu(player, databasePlayer, itemLoadout, tier, item);
                                } else if (e.isRightClick()) {
                                    itemLoadout.getItems().remove(item.getUUID());
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openItemLoadoutMenu(player, itemLoadout, databasePlayer);
                                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 0.1f);
                                }
                            }
                    );
                    equipped = true;
                    equippedItems.remove(item);
                    break;
                }
                if (!equipped) {
                    menu.setItem(x, y + 1,
                            new ItemBuilder(ItemTier.NONE.clayBlock)
                                    .name(Component.text("Click to Equip Item", NamedTextColor.GREEN))
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
                                                   .toList();
        for (int i = 0; i < sortedLoadouts.size(); i++) {
            ItemLoadout l = sortedLoadouts.get(i);
            DifficultyMode difficulty = l.getDifficultyMode();
            Specializations spec = l.getSpec();
            lore.add((l.equals(itemLoadout) ? ChatColor.AQUA : ChatColor.GRAY).toString() + (i + 1) + ". " + l.getName() +
                    " (" + l.getWeight(itemsManager) + " | " + difficulty.getShortName() + " | " + (spec == null ? "Any" : spec.name) + ")");
        }
        menu.setItem(0, 5,
                new ItemBuilder(Material.BOOK)
                        .name(Component.text("Change Loadout (Weight | Difficulty | Spec)", NamedTextColor.GREEN))
                        .loreLEGACY(lore)
                        .get(),
                (m, e) -> {
                    int index = sortedLoadouts.indexOf(itemLoadout);
                    int nextLoadout = index >= sortedLoadouts.size() - 1 ? 0 : index + 1;
                    openItemLoadoutMenu(player, sortedLoadouts.get(nextLoadout), databasePlayer);
                }
        );
        menu.setItem(1, 5,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name(Component.text("Create Loadout", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text("Create a new loadout to customize your experience.", NamedTextColor.GRAY), 150))
                        .get(),
                (m, e) -> {
                    if (itemsManager.getLoadouts().size() >= 9) {
                        player.sendMessage(Component.text("You can only have up to 9 loadouts!", NamedTextColor.RED));
                    } else {
                        new SignGUI()
                                .lines("", "Enter", "Loadout Name", "")
                                .onFinish((p, lines) -> {
                                    String name = lines[0];
                                    if (!name.matches("[a-zA-Z0-9 ]+")) {
                                        player.sendMessage(Component.text("Invalid name!", NamedTextColor.RED));
                                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                        return null;
                                    }
                                    if (loadouts.stream().anyMatch(i -> i.getName().equalsIgnoreCase(name))) {
                                        player.sendMessage(Component.text("You already have a loadout with that name!", NamedTextColor.RED));
                                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                        return null;
                                    }
                                    ItemLoadout newLoadout = new ItemLoadout(name);
                                    loadouts.add(newLoadout);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openItemLoadoutMenuAfterTick(player, databasePlayer, newLoadout);
                                    return null;
                                }).open(player);
                    }
                }
        );
        menu.setItem(2, 5,
                new ItemBuilder(Material.NAME_TAG)
                        .name(Component.text("Rename Loadout", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text("Rename the current loadout.", NamedTextColor.GRAY), 150))
                        .get(),
                (m, e) -> {
                    if (itemLoadout.getName().equals("Default")) {
                        player.sendMessage(Component.text("You cannot rename the default loadout!", NamedTextColor.RED));
                        return;
                    }
                    new SignGUI()
                            .lines("", "Enter", "Loadout Name", "")
                            .onFinish((p, lines) -> {
                                String name = lines[0];
                                if (!name.matches("[a-zA-Z0-9 ]+")) {
                                    player.sendMessage(Component.text("Invalid name!", NamedTextColor.RED));
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                    return null;
                                }
                                if (loadouts.stream().anyMatch(l -> l.getName().equalsIgnoreCase(name))) {
                                    player.sendMessage(Component.text("You already have a loadout with that name!", NamedTextColor.RED));
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                    return null;
                                }
                                itemLoadout.setName(name);
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                openItemLoadoutMenuAfterTick(player, databasePlayer, itemLoadout);
                                return null;
                            }).open(player);
                }
        );

        menu.setItem(3, 5,
                new ItemBuilder(Material.LAVA_BUCKET)
                        .name(Component.text("Delete Loadout", NamedTextColor.RED))
                        .lore(WordWrap.wrap(Component.text("Delete the current loadout.", NamedTextColor.GRAY), 150))
                        .get(),
                (m, e) -> {
                    if (itemLoadout.getName().equals("Default")) {
                        player.sendMessage(Component.text("You cannot delete the default loadout!", NamedTextColor.RED));
                        return;
                    }
                    Menu.openConfirmationMenu0(
                            player,
                            "Delete Loadout",
                            3,
                            Arrays.asList(
                                    Component.textOfChildren(
                                            Component.text("Delete Loadout: ", NamedTextColor.GRAY),
                                            Component.text(itemLoadout.getName(), NamedTextColor.GOLD)
                                    ),
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("WARNING: ", NamedTextColor.RED),
                                            Component.text("This cannot be undone!", NamedTextColor.GRAY)
                                    )
                            ),
                            Menu.GO_BACK,
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
        menu.setItem(4, 5, MENU_BACK, (m, e) -> openItemEquipMenuInternal(player, databasePlayer));
        lore.clear();
        for (int i = 0; i < loadouts.size(); i++) {
            lore.add("" + (loadouts.get(i).equals(itemLoadout) ? ChatColor.AQUA : ChatColor.GRAY) + (i + 1) + ". " + loadouts.get(i).getName());
        }
        menu.setItem(5, 5,
                new ItemBuilder(Material.TRIPWIRE_HOOK)
                        .name(Component.text("Change Loadout Priority", NamedTextColor.GREEN))
                        .loreLEGACY(
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
        DifficultyMode[] difficultyModes = DifficultyMode.VALUES;
        for (DifficultyMode value : difficultyModes) {
            lore.add((itemLoadout.getDifficultyMode() == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name);
        }
        menu.setItem(6, 5,
                new ItemBuilder(Material.COMPARATOR)
                        .name(Component.text("Bind to Mode", NamedTextColor.GREEN))
                        .loreLEGACY(lore)
                        .get(),
                (m, e) -> {
                    itemLoadout.setDifficultyMode(itemLoadout.getDifficultyMode().next());
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
                        .name(Component.text("Bind to Specialization", NamedTextColor.GREEN))
                        .loreLEGACY(lore)
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
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
                }
        );
        menu.openForPlayer(player);
    }

    private static void openItemLoadoutMenuAfterTick(Player player, DatabasePlayer databasePlayer, ItemLoadout itemLoadout) {
        new BukkitRunnable() {
            @Override
            public void run() {
                openItemLoadoutMenu(player, itemLoadout, databasePlayer);
            }
        }.runTaskLater(Warlords.getInstance(), 1);
    }

    private static void addWeightPercentageBar(Menu menu, int maxWeight, int loadoutWeight) {
        double ratio = loadoutWeight * 9d / maxWeight;
        boolean overweight = loadoutWeight > maxWeight;
        for (int i = 0; i < 9; i++) {
            ItemBuilder itemBuilder;
            if (overweight) {
                itemBuilder = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                        .name(Component.text("Overweight!", NamedTextColor.RED));
            } else if (i <= ratio - 1) {
                itemBuilder = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE);
            } else if (i != 0 && i <= ratio - .5) {
                itemBuilder = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE);
            } else {
                itemBuilder = new ItemBuilder(Material.RED_STAINED_GLASS_PANE);
            }
            if (!overweight) {
                itemBuilder.name(Component.text(" "));
            }
            menu.setItem(i, 0,
                    itemBuilder.get(),
                    (m, e) -> {

                    }
            );
        }
    }

    public static void openItemEquipMenu(
            Player player,
            DatabasePlayer databasePlayer,
            ItemLoadout itemLoadout,
            ItemTier tier,
            AbstractItem previousItem
    ) {
        ItemSearchMenu menu = new ItemSearchMenu(
                player, tier.name + " Items",
                (i, m, e) -> {
                    if (previousItem != null) {
                        itemLoadout.getItems().remove(previousItem.getUUID());
                    }
                    itemLoadout.getItems().add(i.getUUID());
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    openItemLoadoutMenu(player, itemLoadout, databasePlayer);
                },
                itemBuilder -> itemBuilder,
                new ItemSearchMenu.PlayerItemMenuSettings(databasePlayer,
                        Specializations.getClass(itemLoadout.getSpec() != null ? itemLoadout.getSpec() : databasePlayer.getLastSpec())
                )
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

package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.customentities.npc.traits.MasterworksFairTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemLoadout;
import com.ebicep.warlords.pve.newitems.NewItemsManager;
import com.ebicep.warlords.pve.newitems.menu.NewItemSearchMenu;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.JavaUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class MasterworksFairMenu {

    public static void openMasterworksFairMenu(Player player) {
        if (MasterworksFairManager.currentFair == null) {
            if (MasterworksFairTrait.startTime != null) {
                player.sendMessage(Component.text("The Masterworks Fair is starting soon!", NamedTextColor.RED));
            } else {
                player.sendMessage(Component.text("The Masterworks Fair is currently closed!", NamedTextColor.RED));
            }
            return;
        }

        Menu menu = new Menu("Masterworks Fair", 9 * 6);
        UUID uuid = player.getUniqueId();
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();

        WeaponsPvE[] values = WeaponsPvE.VALUES;
        int column = 1;
        for (WeaponsPvE value : values) {
            if (value.getPlayerEntries == null) {
                continue;
            }
            List<MasterworksFairPlayerEntry> weaponPlayerEntries = value.getPlayerEntries.apply(MasterworksFairManager.currentFair);
            Optional<MasterworksFairPlayerEntry> playerEntry = getPlayerEntry(weaponPlayerEntries, uuid);

            ItemBuilder itemBuilder;
            if (playerEntry.isEmpty()) {
                itemBuilder = new ItemBuilder(value.glassItem);
                itemBuilder.name(Component.text("Click to submit a " + value.name + " weapon", NamedTextColor.GREEN));
            } else {
                itemBuilder = new ItemBuilder(playerEntry.get().getWeapon().generateItemStack(false));
                addChangeSubmissionLore(itemBuilder);
            }
            menu.setItem(column, 2,
                    itemBuilder.get(),
                    (m, e) -> openSubmissionMenu(player, databasePlayer, value, 1)
            );

            List<MasterworksFairEntry> masterworksFairEntries = databasePlayerPvE.getMasterworksFairEntries();
            menu.setItem(column, 3,
                    getRecentPlacementsItem(masterworksFairEntries.stream()
                                                                    .filter(entry -> !entry.isItemSubmission() && entry.getRarity() == value)
                                                                    .toList(), value.textColor),
                    (m, e) -> {}
            );
            column += 2;
        }

        List<MasterworksFairPlayerEntry> itemPlayerEntries = MasterworksFairManager.currentFair.getItemPlayerEntries();
        Optional<MasterworksFairPlayerEntry> itemPlayerEntry = getPlayerEntry(itemPlayerEntries, uuid);
        ItemBuilder itemSubmissionBuilder;
        if (itemPlayerEntry.isEmpty()) {
            itemSubmissionBuilder = new ItemBuilder(Material.PURPLE_STAINED_GLASS);
            itemSubmissionBuilder.name(Component.text("Click to submit an item", NamedTextColor.GREEN));
        } else {
            itemSubmissionBuilder = itemPlayerEntry.get().getItem().getItemBuilder();
            addChangeSubmissionLore(itemSubmissionBuilder);
        }
        menu.setItem(7, 2, itemSubmissionBuilder.get(), (m, e) -> openItemSubmissionMenu(player, databasePlayer));
        menu.setItem(7, 3,
                getRecentPlacementsItem(databasePlayerPvE.getMasterworksFairEntries()
                                                          .stream()
                                                          .filter(MasterworksFairEntry::isItemSubmission)
                                                          .toList(), NamedTextColor.LIGHT_PURPLE),
                (m, e) -> {}
        );

        ItemBuilder infoItemBuilder = new ItemBuilder(Material.FIREWORK_ROCKET)
                .name(Component.text("Current Submissions", NamedTextColor.GREEN));
        List<Component> infoLore = new ArrayList<>();
        for (WeaponsPvE value : values) {
            if (value.getPlayerEntries != null) {
                List<MasterworksFairPlayerEntry> weaponPlayerEntries = value.getPlayerEntries.apply(MasterworksFairManager.currentFair);
                infoLore.add(value.getTextColoredName()
                                  .append(Component.text(": "))
                                  .append(Component.text(weaponPlayerEntries.size(), NamedTextColor.AQUA))
                );
            }
        }
        infoLore.add(Component.text("Items", NamedTextColor.LIGHT_PURPLE)
                              .append(Component.text(": "))
                              .append(Component.text(itemPlayerEntries.size(), NamedTextColor.AQUA))
        );
        infoItemBuilder.lore(infoLore);
        menu.setItem(4, 0, infoItemBuilder.get(), (m, e) -> {});

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static Optional<MasterworksFairPlayerEntry> getPlayerEntry(List<MasterworksFairPlayerEntry> entries, UUID uuid) {
        return entries.stream().filter(entry -> entry.getUuid().equals(uuid)).findFirst();
    }

    private static void addChangeSubmissionLore(ItemBuilder itemBuilder) {
        itemBuilder.addLore(
                Component.empty(),
                Component.textOfChildren(
                        Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                        Component.text(" to change your submission", NamedTextColor.GREEN)
                )
        );
    }

    private static org.bukkit.inventory.ItemStack getRecentPlacementsItem(List<MasterworksFairEntry> entries, NamedTextColor placementColor) {
        return new ItemBuilder(Material.BOOK)
                .name(Component.text("Your most recent placements", NamedTextColor.GREEN))
                .lore(entries.stream()
                             .collect(JavaUtils.lastN(10))
                             .stream()
                             .sorted(Comparator.comparing(MasterworksFairEntry::getTime).reversed())
                             .map(entry -> Component.text(MasterworksFairManager.FORMATTER.format(entry.getTime()) + ": ", NamedTextColor.GRAY)
                                                    .append(Component.text("#" + entry.getPlacement(), placementColor))
                                                    .append(Component.text(" - "))
                                                    .append(Component.text(entry.getScore(), NamedTextColor.YELLOW)))
                             .collect(Collectors.toList())
                )
                .get();
    }

    public static void openSubmissionMenu(Player player, DatabasePlayer databasePlayer, WeaponsPvE weaponType, int page) {
        Menu menu = new Menu("Choose a weapon", 9 * 6);
        UUID uuid = player.getUniqueId();
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        List<AbstractWeapon> filteredWeaponInventory = new ArrayList<>(weaponInventory);
        filteredWeaponInventory.removeIf(weapon -> weapon.getRarity() != weaponType);
        filteredWeaponInventory.sort(WeaponManagerMenu.SortOptions.WEAPON_SCORE.comparator.reversed());

        List<MasterworksFairPlayerEntry> weaponPlayerEntries = weaponType.getPlayerEntries.apply(MasterworksFairManager.currentFair);
        Optional<MasterworksFairPlayerEntry> playerEntry = getPlayerEntry(weaponPlayerEntries, uuid);

        for (int i = 0; i < 45; i++) {
            int weaponNumber = ((page - 1) * 45) + i;
            if (weaponNumber < filteredWeaponInventory.size()) {
                AbstractWeapon weapon = filteredWeaponInventory.get(weaponNumber);
                int column = i % 9;
                int row = i / 9;

                menu.setItem(column, row, weapon.generateItemStack(false), (m, e) -> {
                    if (weapon.isBound()) {
                        MasterworksFairManager.sendMasterworksFairMessage(player,
                                Component.text("You cannot submit a bound weapon. Unbind it first!", NamedTextColor.RED)
                        );
                        return;
                    }
                    Menu.openConfirmationMenu(
                            player,
                            "Submit Weapon",
                            3,
                            Arrays.asList(
                                    Component.text("Submit ", NamedTextColor.GRAY).append(weapon.getName()),
                                    Component.text("to the Masterworks Fair?", NamedTextColor.GRAY),
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("WARNING: ", NamedTextColor.RED),
                                            Component.text("This will override any previous", NamedTextColor.GRAY)
                                    ),
                                    Component.text("weapon and you cannot get this weapon back!", NamedTextColor.GRAY)
                            ),
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                MasterworksFairPlayerEntry masterworksFairPlayerEntry = playerEntry.orElseGet(() -> new MasterworksFairPlayerEntry(uuid));
                                if (playerEntry.isEmpty()) {
                                    weaponPlayerEntries.add(masterworksFairPlayerEntry);
                                }
                                weaponInventory.remove(weapon);
                                masterworksFairPlayerEntry.setWeapon(weapon);
                                MasterworksFairManager.updateFair.set(true);
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                MasterworksFairManager.sendMasterworksFairMessage(player,
                                        Component.text("Submitted ", NamedTextColor.GRAY)
                                                 .append(weapon.getHoverComponent(false))
                                                 .append(Component.text(" to the Masterworks Fair!"))
                                );
                                openMasterworksFairMenu(player);
                            },
                            (m2, e2) -> openSubmissionMenu(player, databasePlayer, weaponType, page),
                            m2 -> m2.setItem(4, 1, weapon.generateItemStack(false), (m3, e3) -> {})
                    );
                });
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openSubmissionMenu(player, databasePlayer, weaponType, page - 1)
            );
        }
        if (filteredWeaponInventory.size() > (page * 45)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openSubmissionMenu(player, databasePlayer, weaponType, page + 1)
            );
        }

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> openMasterworksFairMenu(player));
        menu.openForPlayer(player);
    }

    public static void openItemSubmissionMenu(Player player, DatabasePlayer databasePlayer) {
        NewItemsManager itemsManager = databasePlayer.getPveStats().getNewItemsManager();
        NewItemSearchMenu.PlayerItemMenuSettings menuSettings = new NewItemSearchMenu.PlayerItemMenuSettings(databasePlayer)
                .setItemInventory(new ArrayList<>(itemsManager.getItemInventory()));
        menuSettings.setSortOption(NewItemSearchMenu.SortOptions.ITEM_SCORE);
        menuSettings.setAscending(false);

        NewItemSearchMenu menu = new NewItemSearchMenu(
                player,
                "Choose an item",
                (item, m, e) -> submitItem(player, databasePlayer, item),
                itemBuilder -> itemBuilder.addLore(
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to submit this item", NamedTextColor.GREEN)
                        )
                ),
                menuSettings,
                databasePlayer,
                m -> m.setItem(4, 5, Menu.MENU_BACK, (m2, e) -> openMasterworksFairMenu(player))
        );
        menu.open();
    }

    private static void submitItem(Player player, DatabasePlayer databasePlayer, NewItem item) {
        NewItemsManager itemsManager = databasePlayer.getPveStats().getNewItemsManager();
        boolean equipped = itemsManager.getLoadouts()
                                       .stream()
                                       .map(NewItemLoadout::getItems)
                                       .flatMap(Collection::stream)
                                       .anyMatch(item.getUUID()::equals);
        if (equipped) {
            MasterworksFairManager.sendMasterworksFairMessage(player,
                    Component.text("You cannot submit an equipped item. Unequip it first!", NamedTextColor.RED)
            );
            return;
        }

        Menu.openConfirmationMenu(
                player,
                "Submit Item",
                3,
                Arrays.asList(
                        Component.text("Submit ", NamedTextColor.GRAY).append(item.getName()),
                        Component.text("to the Masterworks Fair?", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("WARNING: ", NamedTextColor.RED),
                                Component.text("This will override any previous", NamedTextColor.GRAY)
                        ),
                        Component.text("item and you cannot get this item back!", NamedTextColor.GRAY)
                ),
                Menu.GO_BACK,
                (m, e) -> {
                    List<MasterworksFairPlayerEntry> itemPlayerEntries = MasterworksFairManager.currentFair.getItemPlayerEntries();
                    Optional<MasterworksFairPlayerEntry> playerEntry = getPlayerEntry(itemPlayerEntries, player.getUniqueId());
                    MasterworksFairPlayerEntry masterworksFairPlayerEntry = playerEntry.orElseGet(() -> new MasterworksFairPlayerEntry(player.getUniqueId()));
                    if (playerEntry.isEmpty()) {
                        itemPlayerEntries.add(masterworksFairPlayerEntry);
                    }
                    itemsManager.getItemInventory().remove(item);
                    masterworksFairPlayerEntry.setItem(item);
                    MasterworksFairManager.updateFair.set(true);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    MasterworksFairManager.sendMasterworksFairMessage(player,
                            Component.text("Submitted ", NamedTextColor.GRAY)
                                     .append(item.getHoverComponent())
                                     .append(Component.text(" to the Masterworks Fair!"))
                    );
                    openMasterworksFairMenu(player);
                },
                (m, e) -> openItemSubmissionMenu(player, databasePlayer),
                m -> m.setItem(4, 1, item.getItemBuilder().get(), (m2, e2) -> {})
        );
    }

}

package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.weapons.*;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.pve.weapons.menu.WeaponBindMenu.openWeaponBindMenu;

public class WeaponManagerMenu {

    public enum SortOptions {
        NONE("None", (o1, o2) -> o1.getDate().compareTo(o2.getDate())),
        RARITY("Rarity", (o1, o2) -> WeaponsPvE.getWeapon(o1).compareTo(WeaponsPvE.getWeapon(o2))),
        ;
        public final String name;
        public final Comparator<AbstractWeapon> comparator;

        SortOptions(String name, Comparator<AbstractWeapon> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        private static final SortOptions[] vals = values();

        public SortOptions next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    public static HashMap<UUID, PlayerMenuSettings> playerMenuSettings = new HashMap<>();

    static class PlayerMenuSettings {
        private int page = 1;
        private List<AbstractWeapon> weaponInventory = new ArrayList<>();
        private SortOptions sortOption = SortOptions.NONE;
        private boolean ascending = true; //ascending = smallest -> largest/recent

        public void sort() {
            weaponInventory.sort(sortOption.comparator);
            if (!ascending) {
                Collections.reverse(weaponInventory);
            }
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<AbstractWeapon> getWeaponInventory() {
            return weaponInventory;
        }

        public void setWeaponInventory(List<AbstractWeapon> weaponInventory) {
            this.weaponInventory = weaponInventory;
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

    public static void openWeaponInventoryFromExternal(Player player) {
        UUID uuid = player.getUniqueId();
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();

        playerMenuSettings.putIfAbsent(uuid, new PlayerMenuSettings());
        PlayerMenuSettings menuSettings = playerMenuSettings.get(uuid);
        menuSettings.setWeaponInventory(weaponInventory);
        menuSettings.sort();

        openWeaponInventoryFromInternal(player);
    }

    public static void openWeaponInventoryFromInternal(Player player) {
        PlayerMenuSettings menuSettings = playerMenuSettings.get(player.getUniqueId());
        int page = menuSettings.getPage();
        List<AbstractWeapon> weaponInventory = menuSettings.getWeaponInventory();
        SortOptions sortedBy = menuSettings.getSortOption();
        menuSettings.sort();

        Menu menu = new Menu("Weapon Inventory", 9 * 6);

        for (int i = 0; i < 45; i++) {
            int weaponNumber = ((page - 1) * 45) + i;
            if (weaponNumber < weaponInventory.size()) {
                AbstractWeapon abstractWeapon = weaponInventory.get(weaponNumber);

                int column = i % 9;
                int row = i / 9;

                menu.setItem(column, row,
                        abstractWeapon.generateItemStack(),
                        (m, e) -> openWeaponEditor(player, abstractWeapon)
                );
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
                        openWeaponInventoryFromInternal(player);
                    }
            );
        }
        if (weaponInventory.size() > (page * 45)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page + 1);
                        openWeaponInventoryFromInternal(player);
                    }
            );
        }

        menu.setItem(5, 5,
                new ItemBuilder(Material.REDSTONE_COMPARATOR).name(ChatColor.GREEN + "Sort By")
                        .lore(Arrays.stream(SortOptions.values())
                                .map(value -> (sortedBy == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                .collect(Collectors.joining("\n"))
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setSortOption(sortedBy.next());
                    openWeaponInventoryFromInternal(player);
                }
        );
        menu.setItem(6, 5,
                new ItemBuilder(Material.LEVER).name(ChatColor.GREEN + "Sort Order")
                        .lore(menuSettings.isAscending() ?
                                ChatColor.AQUA + "Ascending\n" + ChatColor.GRAY + "Descending" :
                                ChatColor.GRAY + "Ascending\n" + ChatColor.AQUA + "Descending"
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setAscending(!menuSettings.isAscending());
                    openWeaponInventoryFromInternal(player);
                }
        );

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openWeaponEditor(Player player, AbstractWeapon weapon) {
        Menu menu = new Menu("Weapon Editor", 9);

        menu.setItem(
                0,
                0,
                weapon.generateItemStack(),
                (m, e) -> {
                }
        );

        //salvage common/rare/epic/legendary
        menu.setItem(2, 0,
                (weapon instanceof CommonWeapon || weapon instanceof RareWeapon) ?
                        new ItemBuilder(Material.FURNACE)
                                .name(ChatColor.GREEN + "Salvage Weapon")
                                .lore(
                                        ChatColor.GRAY + "Click here to salvage this weapon and claim its materials.",
                                        "",
                                        ChatColor.YELLOW + "Shift-Click" + ChatColor.GRAY + " to instantly salvage this weapon.",
                                        "",
                                        ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This action cannot be undone."
                                )
                                .get() :
                        new ItemBuilder(Material.FURNACE)
                                .name(ChatColor.GREEN + "Salvage Weapon")
                                .lore(
                                        ChatColor.GRAY + "Click here to salvage this weapon and claim its materials.",
                                        "",
                                        ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This action cannot be undone."
                                )
                                .get(),
                (m, e) -> {
                    if ((weapon instanceof CommonWeapon || weapon instanceof RareWeapon) && e.isShiftClick()) {
                        WeaponSalvageMenu.salvageWeapon(player, weapon);
                        openWeaponInventoryFromInternal(player);
                    } else {
                        WeaponSalvageMenu.openWeaponSalvageConfirmMenu(player, weapon);
                    }
                }
        );
        //bind common/rare/epic/legendary
        menu.setItem(3, 0,
                new ItemBuilder(Material.SLIME_BALL)
                        .name(ChatColor.GREEN + "Bind Weapon")
                        .get(),
                (m, e) -> openWeaponBindMenu(player, weapon)
        );
        //fairy essence reskin commmon/rare/epic/legendary
        menu.setItem(4, 0,
                new ItemBuilder(Material.PAINTING)
                        .name(ChatColor.GREEN + "Skin Selector")
                        .get(),
                (m, e) -> {
                    WeaponSkinSelectorMenu.openWeaponSkinSelectorMenu(player, weapon, 1);
                }
        );

        if (weapon instanceof EpicWeapon || weapon instanceof LegendaryWeapon) {
            //synthetic alloy upgrade epic/legendary
            menu.setItem(5, 0,
                    new ItemBuilder(Material.ANVIL)
                            .name(ChatColor.GREEN + "Upgrade Weapon")
                            .get(),
                    (m, e) -> {
                    }
            );
            if (weapon instanceof LegendaryWeapon) {
                //synthetic alloy title legendary
                menu.setItem(6, 0,
                        new ItemBuilder(Material.NAME_TAG)
                                .name(ChatColor.GREEN + "Title Weapon")
                                .get(),
                        (m, e) -> {
                        });
            }
        }

        menu.setItem(8, 0, MENU_BACK, (m, e) -> openWeaponInventoryFromInternal(player));
        menu.openForPlayer(player);
    }


}

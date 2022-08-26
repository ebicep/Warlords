package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.rewards.Currencies;
import com.ebicep.warlords.pve.weapons.*;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import com.ebicep.warlords.pve.weapons.weaponaddons.StatsRerollable;
import com.ebicep.warlords.pve.weapons.weaponaddons.Upgradeable;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.pve.weapons.weapontypes.StarterWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.pve.weapons.menu.WeaponBindMenu.openWeaponBindMenu;

public class WeaponManagerMenu {

    public static final List<Currencies> CURRENCIES_TO_DISPLAY = Arrays.asList(
            Currencies.SYNTHETIC_SHARD,
            Currencies.LEGEND_FRAGMENTS,
            Currencies.FAIRY_ESSENCE,
            Currencies.COMMON_STAR_PIECE,
            Currencies.RARE_STAR_PIECE,
            Currencies.EPIC_STAR_PIECE,
            Currencies.LEGENDARY_STAR_PIECE
    );
    public static HashMap<UUID, PlayerMenuSettings> playerMenuSettings = new HashMap<>();

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
        menuSettings.sort();
        List<AbstractWeapon> weaponInventory = new ArrayList<>(menuSettings.getSortedWeaponInventory());
        weaponInventory.removeIf(weapon -> weapon instanceof StarterWeapon);

        SortOptions sortedBy = menuSettings.getSortOption();
        WeaponsPvE filterBy = menuSettings.getFilter();

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
            } else {
                break;
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

        DatabasePlayerPvE databasePlayerPvE = DatabaseManager.playerService.findByUUID(player.getUniqueId()).getPveStats();
        menu.setItem(1, 5,
                new ItemBuilder(Material.BOOKSHELF)
                        .name(ChatColor.GREEN + "Your Drops")
                        .lore(CURRENCIES_TO_DISPLAY.stream()
                                .map(rewardTypes -> rewardTypes.getColoredName() + (databasePlayerPvE.getCurrencyValue(rewardTypes) != 1 ? "s" : "") + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + databasePlayerPvE.getCurrencyValue(rewardTypes) + (rewardTypes == Currencies.FAIRY_ESSENCE ? "\n" : ""))
                                .collect(Collectors.joining("\n"))
                        )
                        .get(),
                (m, e) -> {
                }
        );
        menu.setItem(3, 5,
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(ChatColor.GREEN + "Reset Settings")
                        .lore(ChatColor.GRAY + "Reset the filter, sort, and order of weapons")
                        .get(),
                (m, e) -> {
                    menuSettings.setFilter(WeaponsPvE.NONE);
                    menuSettings.setSortOption(SortOptions.DATE);
                    menuSettings.setAscending(true);
                    openWeaponInventoryFromInternal(player);
                }
        );
        menu.setItem(5, 5,
                new ItemBuilder(Material.HOPPER)
                        .name(ChatColor.GREEN + "Filter By")
                        .lore(Arrays.stream(WeaponsPvE.values())
                                .map(value -> (filterBy == value ? ChatColor.AQUA : ChatColor.GRAY) + value.name)
                                .collect(Collectors.joining("\n"))
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setFilter(filterBy.next());
                    openWeaponInventoryFromInternal(player);
                }
        );
        menu.setItem(6, 5,
                new ItemBuilder(Material.REDSTONE_COMPARATOR)
                        .name(ChatColor.GREEN + "Sort By")
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
                    openWeaponInventoryFromInternal(player);
                }
        );

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openWeaponEditor(Player player, AbstractWeapon weapon) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer == null) return;

        Menu menu = new Menu("Weapon Editor", 9 * 3);

        menu.setItem(
                4,
                0,
                weapon.generateItemStack(),
                (m, e) -> {
                }
        );

        List<Pair<ItemStack, BiConsumer<Menu, InventoryClickEvent>>> weaponOptions = new ArrayList<>();
        //bind common/rare/epic/legendary
        weaponOptions.add(new Pair<>(
                new ItemBuilder(Material.SLIME_BALL)
                        .name(ChatColor.GREEN + "Bind Weapon")
                        .get(),
                (m, e) -> openWeaponBindMenu(player, weapon)
        ));
        //fairy essence reskin commmon/rare/epic/legendary
        weaponOptions.add(new Pair<>(
                new ItemBuilder(Material.PAINTING)
                        .name(ChatColor.GREEN + "Skin Selector")
                        .get(),
                (m, e) -> WeaponSkinSelectorMenu.openWeaponSkinSelectorMenu(player, weapon, 1)
        ));
        if (weapon instanceof AbstractTierOneWeapon) {
            //star piece
            weaponOptions.add(new Pair<>(
                    new ItemBuilder(Material.NETHER_STAR)
                            .name(ChatColor.GREEN + "Apply a Star Piece")
                            .get(),
                    (m, e) -> {
                        WeaponsPvE weaponsPvE = WeaponsPvE.getWeapon(weapon);
                        if (databasePlayer.getPveStats().getCurrencyValue(weaponsPvE.starPieceCurrency) <= 0) {
                            player.sendMessage(ChatColor.RED + "You do not have any star pieces to apply!");
                            return;
                        }
                        WeaponStarPieceMenu.openWeaponStarPieceMenu(player, (AbstractTierOneWeapon) weapon);
                    }
            ));
        }
        //salvage common/rare/epic
        if (weapon instanceof Salvageable) {
            weaponOptions.add(new Pair<>(
                    !(weapon instanceof AbstractTierTwoWeapon) ?
                            new ItemBuilder(Material.FURNACE)
                                    .name(ChatColor.GREEN + "Salvage Weapon")
                                    .lore(
                                            ChatColor.GRAY + "Click here to salvage this weapon and claim its materials.",
                                            "",
                                            ChatColor.YELLOW + "Shift-Click" + ChatColor.GRAY + " to instantly salvage this weapon.",
                                            "",
                                            ChatColor.GREEN + "Rewards: " + ((Salvageable) weapon).getSalvageRewardMessage(),
                                            "",
                                            ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This action cannot be undone."
                                    )
                                    .get() :
                            new ItemBuilder(Material.FURNACE)
                                    .name(ChatColor.GREEN + "Salvage Weapon")
                                    .lore(
                                            ChatColor.GRAY + "Click here to salvage this weapon and claim its materials.",
                                            "",
                                            ChatColor.GREEN + "Rewards: " + ((Salvageable) weapon).getSalvageRewardMessage(),
                                            "",
                                            ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This action cannot be undone."
                                    )
                                    .get(),
                    (m, e) -> {
                        if (weapon.isBound()) {
                            player.sendMessage(ChatColor.RED + "You cannot salvage a bound weapon!");
                            return;
                        }
                        Specializations weaponSpec = weapon.getSpecializations();
                        List<AbstractWeapon> sameSpecWeapons = databasePlayer.getPveStats().getWeaponInventory()
                                .stream()
                                .filter(w -> w.getSpecializations() == weaponSpec)
                                .collect(Collectors.toList());
                        if (sameSpecWeapons.size() == 1) {
                            player.sendMessage(ChatColor.RED + "You cannot salvage this weapon because you need to have at least one for each specialization!");
                            return;
                        }

                        if (!(weapon instanceof AbstractTierTwoWeapon) && e.isShiftClick()) {
                            WeaponSalvageMenu.salvageWeapon(player, weapon);
                            openWeaponInventoryFromInternal(player);
                        } else {
                            WeaponSalvageMenu.openWeaponSalvageConfirmMenu(player, weapon);
                        }
                    }
            ));
        }
        //reroll common/rare/epic
        if (weapon instanceof StatsRerollable) {
            weaponOptions.add(new Pair<>(
                    new ItemBuilder(Material.WORKBENCH)
                            .name(ChatColor.GREEN + "Weapon Stats Reroll")
                            .get(),
                    (m, e) -> WeaponRerollMenu.openWeaponRerollMenu(player, weapon)
            ));
        }
        //upgrade epic/legendary
        if (weapon instanceof Upgradeable) {
            weaponOptions.add(new Pair<>(
                    new ItemBuilder(Material.ANVIL)
                            .name(ChatColor.GREEN + "Upgrade Weapon")
                            .get(),
                    (m, e) -> WeaponUpgradeMenu.openWeaponUpgradeMenu(player, weapon)
            ));
        }
        if (weapon instanceof AbstractLegendaryWeapon) {
            //synthetic alloy title legendary
            weaponOptions.add(new Pair<>(
                    new ItemBuilder(Material.NAME_TAG)
                            .name(ChatColor.GREEN + "Title Weapon")
                            .get(),
                    (m, e) -> {
                    }
            ));
        }

        for (int i = 0; i < weaponOptions.size(); i++) {
            Pair<ItemStack, BiConsumer<Menu, InventoryClickEvent>> option = weaponOptions.get(i);
            menu.setItem(
                    i % 7 + 1,
                    i / 7 + 1,
                    option.getA(),
                    option.getB()
            );
        }

        menu.setItem(4, 2, MENU_BACK, (m, e) -> openWeaponInventoryFromInternal(player));
        menu.openForPlayer(player);
    }

    public enum SortOptions {
        DATE("Date", (o1, o2) -> o1.getDate().compareTo(o2.getDate())),
        RARITY("Rarity", (o1, o2) -> WeaponsPvE.getWeapon(o1).compareTo(WeaponsPvE.getWeapon(o2))),
        WEAPON_SCORE("Weapon Score", (o1, o2) -> {
            //first check if implements WeaponScore
            if (o1 instanceof WeaponScore && o2 instanceof WeaponScore) {
                return Double.compare(((WeaponScore) o1).getWeaponScore(), ((WeaponScore) o2).getWeaponScore());
            } else {
                //check whether one of them implements WeaponScore
                if (o1 instanceof WeaponScore) {
                    return 1;
                } else if (o2 instanceof WeaponScore) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }),

        ;
        private static final SortOptions[] vals = values();
        public final String name;
        public final Comparator<AbstractWeapon> comparator;

        SortOptions(String name, Comparator<AbstractWeapon> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        public SortOptions next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    static class PlayerMenuSettings {
        private int page = 1;
        private List<AbstractWeapon> weaponInventory = new ArrayList<>();
        private List<AbstractWeapon> sortedWeaponInventory = new ArrayList<>();
        private WeaponsPvE filter = WeaponsPvE.NONE;
        private SortOptions sortOption = SortOptions.DATE;
        private boolean ascending = true; //ascending = smallest -> largest/recent

        public void sort() {
            sortedWeaponInventory = new ArrayList<>(weaponInventory);
            if (filter != WeaponsPvE.NONE) {
                sortedWeaponInventory.removeIf(weapon -> !Objects.equals(weapon.getClass(), filter.weaponClass));
            }
            sortedWeaponInventory.sort(sortOption.comparator);
            if (!ascending) {
                Collections.reverse(sortedWeaponInventory);
            }
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<AbstractWeapon> getSortedWeaponInventory() {
            return sortedWeaponInventory;
        }

        public void setWeaponInventory(List<AbstractWeapon> weaponInventory) {
            this.weaponInventory = weaponInventory;
            this.sortedWeaponInventory = new ArrayList<>(weaponInventory);
        }

        public WeaponsPvE getFilter() {
            return filter;
        }

        public void setFilter(WeaponsPvE filter) {
            this.filter = filter;
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

package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import com.ebicep.warlords.pve.weapons.weaponaddons.StatsRerollable;
import com.ebicep.warlords.pve.weapons.weaponaddons.Upgradeable;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.StarterWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.pve.weapons.menu.WeaponBindMenu.openWeaponBindMenu;

public class WeaponManagerMenu {

    public static final int MAX_WEAPONS_PER_PAGE = 45;
    public static final int MAX_WEAPONS = MAX_WEAPONS_PER_PAGE * 5;
    public static final int MAX_WEAPONS_PATREON = MAX_WEAPONS_PER_PAGE * 10;
    public static final Component GO_BACK = Component.text("Go Back.", NamedTextColor.GRAY);

    public static final HashMap<UUID, PlayerWeaponMenuSettings> PLAYER_MENU_SETTINGS = new HashMap<>();

    public static void openWeaponInventoryFromExternal(Player player, boolean fromNPC) {
        UUID uuid = player.getUniqueId();
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            PLAYER_MENU_SETTINGS.putIfAbsent(uuid, new PlayerWeaponMenuSettings(databasePlayer));
            PlayerWeaponMenuSettings menuSettings = PLAYER_MENU_SETTINGS.get(uuid);
            menuSettings.setOpenedFromNPC(fromNPC);
            menuSettings.sort(PlayerSettings.getPlayerSettings(uuid).getSelectedSpec());

            openWeaponInventoryFromInternal(player, databasePlayer);
        });
    }

    public static void openWeaponInventoryFromInternal(Player player, DatabasePlayer databasePlayer) {
        UUID uuid = player.getUniqueId();
        PLAYER_MENU_SETTINGS.putIfAbsent(uuid, new PlayerWeaponMenuSettings(databasePlayer));
        PlayerWeaponMenuSettings menuSettings = PLAYER_MENU_SETTINGS.get(uuid);
        int page = menuSettings.getPage();
        menuSettings.sort(PlayerSettings.getPlayerSettings(uuid).getSelectedSpec());
        List<AbstractWeapon> weaponInventory = new ArrayList<>(menuSettings.getSortedWeaponInventory());
        weaponInventory.removeIf(weapon -> weapon instanceof StarterWeapon);

        SortOptions sortedBy = menuSettings.getSortOption();
        WeaponsPvE filterBy = menuSettings.getRarityFilter();
        BindFilterOptions bindFilterOption = menuSettings.getBindFilterOption();
        boolean selectedSpecFilter = menuSettings.isSelectedSpecFilter();

        Menu menu = new Menu("Weapon Inventory", 9 * 6);

        for (int i = 0; i < MAX_WEAPONS_PER_PAGE; i++) {
            int weaponNumber = ((page - 1) * MAX_WEAPONS_PER_PAGE) + i;
            if (weaponNumber < weaponInventory.size()) {
                AbstractWeapon abstractWeapon = weaponInventory.get(weaponNumber);

                int column = i % 9;
                int row = i / 9;

                menu.setItem(column, row,
                        abstractWeapon.generateItemStack(true),
                        (m, e) -> openWeaponEditor(player, databasePlayer, abstractWeapon)
                );
            } else {
                break;
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page - 1);
                        openWeaponInventoryFromInternal(player, databasePlayer);
                    }
            );
        }
        if (weaponInventory.size() > (page * MAX_WEAPONS_PER_PAGE)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> {
                        menuSettings.setPage(page + 1);
                        openWeaponInventoryFromInternal(player, databasePlayer);
                    }
            );
        }
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();

        menu.setItem(1, 5,
                new ItemBuilder(Material.FURNACE)
                        .name(Component.text("Salvage All Weapons", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(
                                Component.textOfChildren(
                                        Component.text("LEFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text("to salvage all weapons below ", NamedTextColor.GRAY),
                                        Component.text(menuSettings.getWeaponScoreSalvage() + "% ", NamedTextColor.GREEN),
                                        Component.text("weapon score, excluding bound weapons.", NamedTextColor.GRAY)
                                ), 160))
                        .addLore(Component.empty())
                        .addLore(WordWrap.wrap(
                                Component.textOfChildren(
                                        Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text("to salvage all ", NamedTextColor.GRAY),
                                        Component.text("filtered ", NamedTextColor.GREEN),
                                        Component.text("weapons below ", NamedTextColor.GRAY),
                                        Component.text(menuSettings.getWeaponScoreSalvage() + "% ", NamedTextColor.GREEN),
                                        Component.text("weapon score, excluding bound weapons.", NamedTextColor.GRAY)
                                ), 160))
                        .addLore(Component.empty())
                        .addLore(WordWrap.wrap(
                                Component.textOfChildren(
                                        Component.text("SHIFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text("to change the weapon score filter amount.", NamedTextColor.GRAY)
                                ), 160))
                        .addLore(Component.empty())
                        .addLore(Component.text("This feature is for Patreons only!", NamedTextColor.LIGHT_PURPLE))
                        .get(),
                (m, e) -> {
                    if (!(player.hasPermission("group.patreon") || player.hasPermission("group.contentcreator")) && !Permissions.isAdmin(player)) {
                        player.sendMessage(Component.text("You must be a Patreon to use this feature!", NamedTextColor.RED));
                        return;
                    }
                    if (e.isShiftClick()) {
                        menuSettings.nextWeaponScoreSalvage();
                        openWeaponInventoryFromInternal(player, databasePlayer);
                        return;
                    }
                    List<AbstractWeapon> weaponsToSalvage;
                    if (e.isLeftClick()) {
                        weaponsToSalvage = new ArrayList<>(databasePlayerPvE.getWeaponInventory());
                    } else {
                        weaponsToSalvage = new ArrayList<>(menuSettings.getSortedWeaponInventory());
                    }
                    weaponsToSalvage.removeIf(weapon -> weapon instanceof StarterWeapon ||
                            !(weapon instanceof WeaponScore) ||
                            !(weapon instanceof Salvageable) ||
                            weapon.isBound() ||
                            ((WeaponScore) weapon).getWeaponScore() >= menuSettings.getWeaponScoreSalvage());
                    if (weaponsToSalvage.isEmpty()) {
                        player.sendMessage(Component.text("No weapons to salvage!", NamedTextColor.RED));
                        return;
                    }
                    List<Component> salvageLore = new ArrayList<>();
                    salvageLore.add(Component.text("Salvage Weapons:", NamedTextColor.GRAY));
                    for (int i = 0; i < weaponsToSalvage.size(); i++) {
                        AbstractWeapon weapon = weaponsToSalvage.get(i);
                        salvageLore.add(Component.textOfChildren(
                                Component.text(" - ", NamedTextColor.GRAY)
                                         .append(weapon.getName()),
                                Component.text(" (" + ((WeaponScore) weapon).getWeaponScore() + ")", NamedTextColor.YELLOW)
                        ));
                        if (i > 50) {
                            salvageLore.add(Component.text(" - . . .", NamedTextColor.GRAY));
                            break;
                        }
                    }
                    Menu.openConfirmationMenu(player,
                            "Confirm Salvaging Weapons",
                            3,
                            salvageLore,
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                for (AbstractWeapon weapon : weaponsToSalvage) {
                                    WeaponSalvageMenu.salvageWeapon(player, databasePlayer, (AbstractWeapon & Salvageable) weapon);
                                }
                                openWeaponInventoryFromExternal(player, true);
                            },
                            (m2, e2) -> openWeaponInventoryFromInternal(player, databasePlayer),
                            (m2) -> {
                            }
                    );
                }
        );
        menu.setItem(2, 5,
                new ItemBuilder(Material.BOOK)
                        .name(Component.text("Your Weapon Drops", NamedTextColor.DARK_AQUA))
                        .lore(Currencies.STAR_PIECES
                                .stream()
                                .map(starPiece -> starPiece.getCostColoredName(databasePlayerPvE.getCurrencyValue(starPiece)))
                                .collect(Collectors.toList())
                        )
                        .addLore(
                                Component.empty(),
                                Currencies.SKILL_BOOST_MODIFIER.getCostColoredName(databasePlayerPvE.getCurrencyValue(Currencies.SKILL_BOOST_MODIFIER)),
                                Currencies.LIMIT_BREAKER.getCostColoredName(databasePlayerPvE.getCurrencyValue(Currencies.LIMIT_BREAKER)),
                                Component.empty(),
                                Currencies.TITLE_TOKEN_JUGGERNAUT.getCostColoredName(databasePlayerPvE.getCurrencyValue(Currencies.TITLE_TOKEN_JUGGERNAUT)),
                                Currencies.TITLE_TOKEN_PHARAOHS_REVENGE.getCostColoredName(databasePlayerPvE.getCurrencyValue(Currencies.TITLE_TOKEN_PHARAOHS_REVENGE)),
                                Currencies.TITLE_TOKEN_SPIDERS_BURROW.getCostColoredName(databasePlayerPvE.getCurrencyValue(Currencies.TITLE_TOKEN_SPIDERS_BURROW)),
                                Currencies.TITLE_TOKEN_BANE_OF_IMPURITIES.getCostColoredName(databasePlayerPvE.getCurrencyValue(Currencies.TITLE_TOKEN_BANE_OF_IMPURITIES)),
                                Currencies.TITLE_TOKEN_GARDEN_OF_HESPERIDES.getCostColoredName(databasePlayerPvE.getCurrencyValue(Currencies.TITLE_TOKEN_GARDEN_OF_HESPERIDES)),
                                Currencies.TITLE_TOKEN_LIBRARY_ARCHIVES.getCostColoredName(databasePlayerPvE.getCurrencyValue(Currencies.TITLE_TOKEN_LIBRARY_ARCHIVES))
                        )
                        .get(),
                (m, e) -> {
                }
        );
        menu.setItem(3, 5,
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(Component.text("Reset Settings", NamedTextColor.GREEN))
                        .lore(Component.text("Reset the filter, sort, and order of weapons", NamedTextColor.GRAY))
                        .get(),
                (m, e) -> {
                    menuSettings.reset();
                    openWeaponInventoryFromInternal(player, databasePlayer);
                }
        );
        menu.setItem(5, 5,
                new ItemBuilder(Material.HOPPER)
                        .name(Component.text("Filter By", NamedTextColor.GREEN))
                        .lore(Arrays.stream(WeaponsPvE.VALUES)
                                    .map(value -> Component.text(value.name, filterBy == value ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                                    .collect(Collectors.toList())
                        )
                        .addLore(Component.textOfChildren(
                                        Component.text("LEFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text("to change rarity filter", NamedTextColor.GREEN)
                                ),
                                Component.empty()
                        )
                        .addLore(Arrays.stream(BindFilterOptions.VALUES)
                                       .map(value -> Component.text(value.name, bindFilterOption == value ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                                       .collect(Collectors.toList())
                        )
                        .addLore(Component.textOfChildren(
                                        Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text("to change bind filter", NamedTextColor.GREEN)
                                ),
                                Component.empty()
                        )
                        .addLore(
                                Component.text("None", selectedSpecFilter ? NamedTextColor.GRAY : NamedTextColor.AQUA),
                                Component.text("Selected Spec", selectedSpecFilter ? NamedTextColor.AQUA : NamedTextColor.GRAY),
                                Component.textOfChildren(
                                        Component.text("SHIFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text("to change spec filter", NamedTextColor.GREEN)
                                )
                        )
                        .get(),
                (m, e) -> {
                    if (e.isShiftClick()) {
                        menuSettings.toggleSelectedSpecFilter();
                    } else if (e.isLeftClick()) {
                        menuSettings.setRarityFilter(filterBy.next());
                    } else if (e.isRightClick()) {
                        menuSettings.setBindFilterOption(bindFilterOption.next());
                    }
                    menuSettings.setPage(1);
                    openWeaponInventoryFromInternal(player, databasePlayer);
                }
        );
        menu.setItem(6, 5,
                new ItemBuilder(Material.COMPARATOR)
                        .name(Component.text("Sort By", NamedTextColor.GREEN))
                        .lore(Arrays.stream(SortOptions.VALUES)
                                    .map(value -> Component.text(value.name, sortedBy == value ? NamedTextColor.AQUA : NamedTextColor.GRAY))
                                    .collect(Collectors.toList())
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setSortOption(sortedBy.next());
                    openWeaponInventoryFromInternal(player, databasePlayer);
                }
        );
        menu.setItem(7, 5,
                new ItemBuilder(Material.LEVER)
                        .name(Component.text("Sort Order", NamedTextColor.GREEN))
                        .lore(menuSettings.isAscending() ?
                              Arrays.asList(
                                      Component.text("Ascending", NamedTextColor.AQUA),
                                      Component.text("Descending", NamedTextColor.GRAY)
                              ) :
                              Arrays.asList(
                                      Component.text("Ascending", NamedTextColor.GRAY),
                                      Component.text("Descending", NamedTextColor.AQUA)
                              )
                        )
                        .get(),
                (m, e) -> {
                    menuSettings.setAscending(!menuSettings.isAscending());
                    openWeaponInventoryFromInternal(player, databasePlayer);
                }
        );

        if (menuSettings.isOpenedFromNPC()) {
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        } else {
            menu.setItem(4, 5, WarlordsNewHotbarMenu.PvEMenu.MENU_BACK_PVE, (m, e) -> WarlordsNewHotbarMenu.PvEMenu.openPvEMenu(player));
        }
        menu.openForPlayer(player);
    }

    public static void openWeaponEditor(Player player, DatabasePlayer databasePlayer, AbstractWeapon weapon) {
        List<Pair<ItemStack, BiConsumer<Menu, InventoryClickEvent>>> weaponOptions = new ArrayList<>();
        //bind common/rare/epic/legendary
        boolean isBound = weapon.isBound();
        ItemBuilder bindWeapon = new ItemBuilder(Material.SLIME_BALL)
                .name(Component.text("Bind Weapon", NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text(
                                "Only the weapon bound to your selected specialization will be used in game.", NamedTextColor.GRAY),
                        180
                ))
                .addLore(Component.empty())
                .addLore(WordWrap.wrap(
                        Component.textOfChildren(
                                Component.text("LEFT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to bind this weapon to your its specialization", NamedTextColor.GREEN)
                        ),
                        180
                ))
                .addLore(WordWrap.wrap(
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to view all bound weapons", NamedTextColor.GREEN)
                        ),
                        180
                ));
        if (isBound) {
            bindWeapon.enchant(Enchantment.OXYGEN, 1);
        }
        weaponOptions.add(new Pair<>(
                bindWeapon.get(),
                (m, e) -> {
                    if (e.isLeftClick()) {
                        if (isBound) {
                            player.sendMessage(Component.text("This weapon is already bound!", NamedTextColor.RED));
                        } else {
                            WeaponBindMenu.bindWeaponDirectly(player, databasePlayer, weapon);
                            openWeaponEditor(player, databasePlayer, weapon);
                        }
                    } else if (e.isRightClick()) {
                        openWeaponBindMenu(player, databasePlayer, weapon);
                    }
                }
        ));
        //fairy essence reskin commmon/rare/epic/legendary
        weaponOptions.add(new Pair<>(
                new ItemBuilder(Material.PAINTING)
                        .name(Component.text("Skin Selector", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text(
                                        "Change the skin of your weapon to better match your tastes.", NamedTextColor.GRAY),
                                180
                        ))
                        .get(),
                (m, e) -> WeaponSkinSelectorMenu.openWeaponSkinSelectorMenu(player, databasePlayer, weapon, 1)
        ));
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        //salvage common/rare/epic
        if (weapon instanceof Salvageable) {
            weaponOptions.add(new Pair<>(
                    !(weapon instanceof EpicWeapon) ?
                    new ItemBuilder(Material.FURNACE)
                            .name(Component.text("Salvage Weapon", NamedTextColor.GREEN))
                            .lore(
                                    Component.text("Click here to salvage this weapon and claim its materials.", NamedTextColor.GRAY),
                                    Component.empty(),
                                    Component.text("Shift-Click", NamedTextColor.YELLOW)
                                             .append(Component.text(" to instantly salvage this weapon.", NamedTextColor.GRAY)),
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("Rewards: ", NamedTextColor.GREEN),
                                            ((Salvageable) weapon).getSalvageRewardMessage()
                                    ),
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("WARNING: ", NamedTextColor.RED),
                                            Component.text("This action cannot be undone.", NamedTextColor.GRAY)
                                    )
                            )
                            .get() :
                    new ItemBuilder(Material.FURNACE)
                            .name(Component.text("Salvage Weapon", NamedTextColor.GREEN))
                            .lore(
                                    Component.text("Click here to salvage this weapon and claim its materials.", NamedTextColor.GRAY),
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("Rewards: ", NamedTextColor.GREEN),
                                            ((Salvageable) weapon).getSalvageRewardMessage()
                                    ),
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("WARNING: ", NamedTextColor.RED),
                                            Component.text("This action cannot be undone.", NamedTextColor.GRAY)
                                    )
                            )
                            .get(),
                    (m, e) -> {
                        if (isBound) {
                            player.sendMessage(Component.text("You cannot salvage a bound weapon!", NamedTextColor.RED));
                            return;
                        }
                        Specializations weaponSpec = weapon.getSpecializations();
                        List<AbstractWeapon> sameSpecWeapons = pveStats.getWeaponInventory()
                                                                       .stream()
                                                                       .filter(w -> w.getSpecializations() == weaponSpec)
                                                                       .toList();
                        if (sameSpecWeapons.size() == 1) {
                            player.sendMessage(Component.text("You cannot salvage this weapon because you need to have at least one for each specialization!",
                                    NamedTextColor.RED
                            ));
                            return;
                        }

                        if (!(weapon instanceof EpicWeapon) && e.isShiftClick()) {
                            WeaponSalvageMenu.salvageWeapon(player, databasePlayer, (AbstractWeapon & Salvageable) weapon);
                            openWeaponInventoryFromInternal(player, databasePlayer);
                        } else {
                            WeaponSalvageMenu.openWeaponSalvageConfirmMenu(player, databasePlayer, (AbstractWeapon & Salvageable) weapon);
                        }
                    }
            ));
        }
        //reroll common/rare/epic
        if (weapon instanceof StatsRerollable) {
            weaponOptions.add(new Pair<>(
                    new ItemBuilder(Material.CRAFTING_TABLE)
                            .name(Component.text("Weapon Stats Reroll", NamedTextColor.GREEN))
                            .lore(((StatsRerollable) weapon).getRerollCostLore())
                            .get(),
                    (m, e) -> {
                        int rerollCost = ((StatsRerollable) weapon).getRerollCost();
                        if (pveStats.getCurrencyValue(Currencies.COIN) < rerollCost) {
                            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                        .append(Currencies.COIN.getCostColoredName(rerollCost))
                                                        .append(Component.text(" to reroll the stats of this weapon!"))
                            );
                            return;
                        }
                        WeaponRerollMenu.openWeaponRerollMenu(player, databasePlayer, (AbstractWeapon & StatsRerollable) weapon);
                    }
            ));
        }
        //upgrade epic/legendary
        if (weapon instanceof Upgradeable upgradeable) {
            weaponOptions.add(new Pair<>(
                    new ItemBuilder(Material.ANVIL)
                            .name(Component.text("Upgrade Weapon", NamedTextColor.GREEN))
                            .lore(upgradeable.getUpgradeCostLore())
                            .get(),
                    (m, e) -> {
                        if (upgradeable.getUpgradeLevel() >= upgradeable.getMaxUpgradeLevel()) {
                            player.sendMessage(Component.text("You can't upgrade this weapon anymore.", NamedTextColor.RED));
                            return;
                        }
                        LinkedHashMap<Currencies, Long> upgradeCost = upgradeable.getUpgradeCost(upgradeable.getUpgradeLevel() + 1);
                        for (Map.Entry<Currencies, Long> currenciesLongEntry : upgradeCost.entrySet()) {
                            Currencies currency = currenciesLongEntry.getKey();
                            Long cost = currenciesLongEntry.getValue();
                            if (pveStats.getCurrencyValue(currency) < cost) {
                                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                            .append(currency.getCostColoredName(cost))
                                                            .append(Component.text(" to upgrade this weapon!"))
                                );
                                return;
                            }
                        }
                        WeaponUpgradeMenu.openWeaponUpgradeMenu(player, databasePlayer, (AbstractWeapon & Upgradeable) weapon);
                    }
            ));
        }
        if (weapon instanceof AbstractLegendaryWeapon legendaryWeapon) {
            PLAYER_MENU_SETTINGS.putIfAbsent(player.getUniqueId(), new PlayerWeaponMenuSettings(databasePlayer));
            PlayerWeaponMenuSettings menuSettings = PLAYER_MENU_SETTINGS.get(player.getUniqueId());
            StarPieces selectedStarPiece = menuSettings.getSelectedStarPiece();
            //star piece
            weaponOptions.add(new Pair<>(
                            new ItemBuilder(Material.NETHER_STAR)
                                    .name(Component.text("Apply a " + selectedStarPiece.currency.name, NamedTextColor.GREEN))
                                    .lore(WordWrap.wrap(
                                            Component.text("This star piece provides a ", NamedTextColor.GRAY)
                                                     .append(Component.text(selectedStarPiece.starPieceBonusValue + "% ", selectedStarPiece.currency.textColor))
                                                     .append(Component.text("stat boost to a random stat.")),
                                            180
                                    ))
                                    .addLore(legendaryWeapon.getStarPieceCostLore(selectedStarPiece))
                                    .addLore(Component.empty(),
                                            Component.textOfChildren(
                                                    Component.text("LEFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                                    Component.text("to apply star piece.", NamedTextColor.GRAY)
                                            ),
                                            Component.textOfChildren(
                                                    Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                                    Component.text("to change star piece selection.", NamedTextColor.GRAY)
                                            )
                                    )

                                    .get(),
                            (m, e) -> {
                                if (e.isLeftClick()) {
                                    for (Map.Entry<Currencies, Long> currenciesLongEntry : legendaryWeapon.getStarPieceBonusCost(selectedStarPiece)
                                                                                                          .entrySet()
                                    ) {
                                        Currencies currency = currenciesLongEntry.getKey();
                                        Long cost = currenciesLongEntry.getValue();
                                        if (pveStats.getCurrencyValue(currency) < cost) {
                                            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                                        .append(currency.getCostColoredName(cost))
                                                                        .append(Component.text(" to apply this star piece!"))
                                            );
                                            return;
                                        }
                                    }
                                    WeaponStarPieceMenu.openWeaponStarPieceMenu(player, databasePlayer, legendaryWeapon, selectedStarPiece);
                                } else if (e.isRightClick()) {
                                    menuSettings.setSelectedStarPiece(selectedStarPiece.next());
                                    openWeaponEditor(player, databasePlayer, weapon);
                                }
                            }
                    )
            );
            List<Component> upgradeWeaponTitleLore = new ArrayList<>(
                    WordWrap.wrap(Component.text("Change your weapon title to modify its stat distribution.", NamedTextColor.GRAY), 180)
            );
            upgradeWeaponTitleLore.add(Component.empty());
            upgradeWeaponTitleLore.add(Component.textOfChildren(
                    Component.text("Upgrade Weapon Title ", NamedTextColor.GREEN),
                    Component.text("[RIGHT-CLICK]", NamedTextColor.YELLOW, TextDecoration.BOLD)
            ));
            upgradeWeaponTitleLore.addAll(WordWrap.wrap(Component.text("Upgrade your weapon title to increase its passive effect.", NamedTextColor.GRAY), 180));
            upgradeWeaponTitleLore.addAll(legendaryWeapon.getTitleUpgradeCostLore());
            weaponOptions.add(new Pair<>(
                    new ItemBuilder(Material.NAME_TAG)
                            .name(Component.textOfChildren(
                                    Component.text("Change Weapon Title ", NamedTextColor.GREEN),
                                    Component.text("[LEFT-CLICK]", NamedTextColor.YELLOW, TextDecoration.BOLD)
                            ))
                            .lore(upgradeWeaponTitleLore)
                            .get(),
                    (m, e) -> {
                        if (e.isLeftClick()) {
                            WeaponTitleMenu.openWeaponTitleMenu(player, databasePlayer, legendaryWeapon, 1);
                        } else if (e.isRightClick()) {
                            if (legendaryWeapon.getTitleUpgradeCost(legendaryWeapon.getTitleLevelUpgraded()) == null) {
                                player.sendMessage(Component.text("This title level upgrade is currently unavailable!", NamedTextColor.RED));
                                return;
                            }
                            if (legendaryWeapon.getTitleLevel() >= legendaryWeapon.getMaxUpgradeLevel()) {
                                player.sendMessage(Component.text("You can't upgrade this weapon title anymore.", NamedTextColor.RED));
                                return;
                            }
                            if (legendaryWeapon.getTitleLevelUpgraded() > legendaryWeapon.getUpgradeLevel()) {
                                player.sendMessage(Component.text("Weapon title level cannot be higher than weapon level. Upgrade your weapon first.", NamedTextColor.RED));
                                return;
                            }
                            for (Map.Entry<Spendable, Long> enumLongEntry : legendaryWeapon.getTitleUpgradeCost(legendaryWeapon.getTitleLevelUpgraded())
                                                                                           .entrySet()
                            ) {
                                Spendable spendable = enumLongEntry.getKey();
                                Long currencyCost = enumLongEntry.getValue();
                                if (spendable.getFromPlayer(databasePlayer) < currencyCost) {
                                    player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                                .append(spendable.getCostColoredName(currencyCost))
                                                                .append(Component.text(" to upgrade this title!"))
                                    );
                                    return;
                                }
                            }
                            WeaponTitleMenu.openWeaponTitleUpgradeMenu(player, databasePlayer, legendaryWeapon);
                        }
                    }
            ));
            weaponOptions.add(new Pair<>(
                    new ItemBuilder(Material.BOOKSHELF)
                            .name(Component.text("Change Skill Boost", NamedTextColor.GREEN))
                            .lore(WordWrap.wrap(Component.text(
                                            "Change the skill boost of this weapon.", NamedTextColor.GRAY),
                                    180
                            ))
                            .get(),
                    (m, e) -> {
                        WeaponSkillBoostMenu.openWeaponSkillBoostMenu(player, databasePlayer, legendaryWeapon);
                    }
            ));


        }

        boolean bigMenu = weaponOptions.size() > 3;
        int rows = bigMenu ? 6 : 5;
        Menu menu = new Menu("Weapon Editor", 9 * rows);

        menu.setItem(
                4,
                0,
                weapon.generateItemStack(false),
                (m, e) -> {
                }
        );

        int x = 2;
        int y = bigMenu ? 1 : 2;
        for (Pair<ItemStack, BiConsumer<Menu, InventoryClickEvent>> option : weaponOptions) {
            menu.setItem(
                    x,
                    y,
                    option.getA(),
                    option.getB()
            );
            x += 2;
            if (x > 6) {
                x = 2;
                y += 2;
            }
        }

        menu.setItem(4, rows - 1, MENU_BACK, (m, e) -> openWeaponInventoryFromInternal(player, databasePlayer));
        menu.openForPlayer(player);
    }

    public enum SortOptions {

        DATE("Date", Comparator.comparing(AbstractWeapon::getDate)),
        RARITY("Rarity", Comparator.comparing(AbstractWeapon::getRarity)),
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

        private static final SortOptions[] VALUES = values();
        public final String name;
        public final Comparator<AbstractWeapon> comparator;

        SortOptions(String name, Comparator<AbstractWeapon> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        public SortOptions next() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }
    }

    public enum BindFilterOptions {

        NONE("None", weapon -> true),
        BOUND("Bound", weapon -> weapon.isBound()),
        UNBOUND("Unbound", weapon -> !weapon.isBound()),

        ;

        public static final BindFilterOptions[] VALUES = values();
        public final String name;
        public final Predicate<AbstractWeapon> filter;

        BindFilterOptions(String name, Predicate<AbstractWeapon> filter) {
            this.name = name;
            this.filter = filter;
        }

        public BindFilterOptions next() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }

    }

    static class PlayerWeaponMenuSettings {
        private boolean openedFromNPC = false;
        private int page = 1;
        private List<AbstractWeapon> weaponInventory = new ArrayList<>();
        private List<AbstractWeapon> sortedWeaponInventory = new ArrayList<>();
        private WeaponsPvE rarityFilter = WeaponsPvE.NONE;
        private BindFilterOptions bindFilterOption = BindFilterOptions.NONE;
        private boolean selectedSpecFilter = false;
        private SortOptions sortOption = SortOptions.DATE;
        private boolean ascending = true; //ascending = smallest -> largest/recent
        private StarPieces selectedStarPiece = StarPieces.COMMON;
        private int weaponScoreSalvage = 70;

        public PlayerWeaponMenuSettings(DatabasePlayer databasePlayer) {
            setWeaponInventory(databasePlayer.getPveStats().getWeaponInventory());
        }

        public void setWeaponInventory(List<AbstractWeapon> weaponInventory) {
            this.weaponInventory = weaponInventory;
            this.sortedWeaponInventory = new ArrayList<>(weaponInventory);
        }

        public void reset() {
            this.page = 1;
            this.rarityFilter = WeaponsPvE.NONE;
            this.bindFilterOption = BindFilterOptions.NONE;
            this.selectedSpecFilter = false;
            this.sortOption = SortOptions.DATE;
            this.ascending = true;
        }

        public void sort(Specializations selectedSpec) {
            sortedWeaponInventory = new ArrayList<>(weaponInventory);
            if (rarityFilter != WeaponsPvE.NONE) {
                sortedWeaponInventory.removeIf(weapon -> weapon.getRarity() != rarityFilter);
            }
            if (bindFilterOption != BindFilterOptions.NONE) {
                sortedWeaponInventory.removeIf(weapon -> !bindFilterOption.filter.test(weapon));
            }
            if (selectedSpecFilter) {
                sortedWeaponInventory.removeIf(weapon -> weapon.getSpecializations() != selectedSpec);
            }
            sortedWeaponInventory.sort(sortOption.comparator);
            if (!ascending) {
                Collections.reverse(sortedWeaponInventory);
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

        public List<AbstractWeapon> getSortedWeaponInventory() {
            return sortedWeaponInventory;
        }

        public WeaponsPvE getRarityFilter() {
            return rarityFilter;
        }

        public void setRarityFilter(WeaponsPvE rarityFilter) {
            this.rarityFilter = rarityFilter;
        }

        public BindFilterOptions getBindFilterOption() {
            return bindFilterOption;
        }

        public void setBindFilterOption(BindFilterOptions bindFilterOption) {
            this.bindFilterOption = bindFilterOption;
        }

        public boolean isSelectedSpecFilter() {
            return selectedSpecFilter;
        }

        public void toggleSelectedSpecFilter() {
            this.selectedSpecFilter = !this.selectedSpecFilter;
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

        public StarPieces getSelectedStarPiece() {
            return Objects.requireNonNullElseGet(selectedStarPiece, () -> selectedStarPiece = StarPieces.COMMON);
        }

        public void setSelectedStarPiece(StarPieces selectedStarPiece) {
            this.selectedStarPiece = selectedStarPiece;
        }

        public int getWeaponScoreSalvage() {
            return weaponScoreSalvage;
        }

        public void nextWeaponScoreSalvage() {
            weaponScoreSalvage += 10;
            if (weaponScoreSalvage > 100) {
                weaponScoreSalvage = 70;
            }
        }

    }


}

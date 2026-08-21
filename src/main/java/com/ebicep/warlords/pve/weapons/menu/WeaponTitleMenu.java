package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.events.WeaponTitlePurchaseEvent;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeaponTitleInfo;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.exception.SignGUIVersionException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;
import static com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu.openWeaponEditor;

public class WeaponTitleMenu {

    private static final int MAX_FAVORITE_TITLES = 5;
    private static final int[] FAVORITE_SLOTS = {2, 3, 4, 5, 6};

    public static void openWeaponTitleMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, LegendaryTitles[] titles, int page) {
        Menu menu = new Menu("Apply Title to Weapon", 9 * 6);

        for (int i = 0; i < 9 * 5; i++) {
            menu.addItem(
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .name(Component.text(" "))
                            .get(),
                    (m, e) -> {
                    }
            );
        }

        menu.setItem(
                4,
                0,
                weapon.generateItemStack(false),
                (m, e) -> {
                }
        );

        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        List<String> favoriteTitleIds = pveStats.getFavoriteWeaponTitles();
        Map<LegendaryTitles, LegendaryWeaponTitleInfo> unlockedTitles = weapon.getTitles();
        for (int i = 0; i < 9; i++) {
            int titleIndex = ((page - 1) * 9) + i;
            if (titleIndex < titles.length) {
                LegendaryTitles title = titles[titleIndex];
                if (!title.isEnabled) {
                    continue;
                }
                AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);
                ItemBuilder itemBuilder = new ItemBuilder(titledWeapon.generateItemStack(false));

                List<Component> loreCost = titledWeapon.getCostLore();

                boolean equals = Objects.equals(weapon.getTitle(), title);
                boolean titleIsLocked = !unlockedTitles.containsKey(title);
                boolean favorite = favoriteTitleIds.contains(title.name());
                if (equals) {
                    itemBuilder.addLore(
                            Component.empty(),
                            Component.text("Selected", NamedTextColor.GREEN)
                    );
                    itemBuilder.enchant(Enchantment.RESPIRATION, 1);
                } else if (titleIsLocked) {
                    itemBuilder.addLore(loreCost);
                } else {
                    itemBuilder.addLore(
                            Component.empty(),
                            Component.text("Click to Select", NamedTextColor.GREEN)
                    );
                }
                if (title != LegendaryTitles.NONE) {
                    itemBuilder.addLore(
                            Component.empty(),
                            favorite
                                    ? Component.text("★ Favorite", NamedTextColor.GOLD)
                                    : Component.text("Right-Click to Favorite", NamedTextColor.YELLOW)
                    );
                    if (favorite) {
                        itemBuilder.addLore(Component.text("Right-Click to Remove Favorite", NamedTextColor.RED));
                    }
                }
                for (int k = 0; k < 1; k++) {
                    for (int j = 0; j < 3; j++) {
                        menu.setItem(
                                k + i,
                                j + 1,
                                new ItemBuilder(title.glassPane)
                                        .name(Component.text(" "))
                                        .get(),
                                (m, e) -> {
                                }
                        );
                    }
                }
                menu.setItem(i, 2,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (e.isRightClick() && title != LegendaryTitles.NONE) {
                                toggleFavoriteTitle(player, databasePlayer, weapon, titles, page, title);
                                return;
                            }
                            openTitleConfirmation(player, databasePlayer, weapon, titles, page, title);
                        }
                );
            }
        }

        addFavoriteTitlesRow(menu, player, databasePlayer, weapon, titles, page);

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openWeaponTitleMenu(player, databasePlayer, weapon, titles, page - 1)
            );
        }
        if (titles.length > (page * 9)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openWeaponTitleMenu(player, databasePlayer, weapon, titles, page + 1)
            );
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openWeaponEditor(player, databasePlayer, weapon));
        menu.setItem(5, 5,
                new ItemBuilder(Material.OAK_SIGN)
                        .name(Component.text("Search Title", NamedTextColor.GREEN))
                        .get(),
                (m, e) ->
                {
                    try {
                        SignGUI.builder()
                               .setLines("", "^ Search Query ^", "Returns titles", "containing query")
                               .setHandler((p, lines) -> {
                                   String titleName = lines.getLine(0);
                                   if (titleName.isEmpty()) {
                                       player.sendMessage(Component.text("Query cannot be empty!", NamedTextColor.RED));
                                       openWeaponEditorAfterTick(player, databasePlayer, weapon);
                                       return null;
                                   }
                                   titleName = titleName.toLowerCase();
                                   String finalTitleName = titleName;
                                   LegendaryTitles[] legendaryTitles = Arrays.stream(LegendaryTitles.VALUES)
                                                                             .filter(title -> title.name.toLowerCase().contains(finalTitleName))
                                                                             .toArray(LegendaryTitles[]::new);
                                    if (legendaryTitles.length == 0) {
                                        player.sendMessage(Component.text("No titles with that name found!", NamedTextColor.RED));
                                        openWeaponEditorAfterTick(player, databasePlayer, weapon);
                                    } else {
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                openWeaponTitleMenu(player, databasePlayer, weapon, legendaryTitles, 1);
                                            }
                                        }.runTaskLater(Warlords.getInstance(), 1);
                                    }
                                   return null;
                               }).build().open(player);
                    } catch (SignGUIVersionException ex) {
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage(ex);
                    }
                }
        );
        menu.openForPlayer(player);
    }

    private static void addFavoriteTitlesRow(
            Menu menu,
            Player player,
            DatabasePlayer databasePlayer,
            AbstractLegendaryWeapon weapon,
            LegendaryTitles[] titles,
            int page
    ) {
        List<LegendaryTitles> favoriteTitles = databasePlayer.getPveStats()
                                                              .getFavoriteWeaponTitles()
                                                              .stream()
                                                              .map(WeaponTitleMenu::getTitleById)
                                                              .filter(Objects::nonNull)
                                                              .filter(title -> title != LegendaryTitles.NONE && title.isEnabled)
                                                              .limit(MAX_FAVORITE_TITLES)
                                                              .toList();

        menu.setItem(
                0,
                4,
                new ItemBuilder(Material.NETHER_STAR)
                        .name(Component.text("Favorite Titles", NamedTextColor.GOLD))
                        .lore(
                                Component.text(favoriteTitles.size() + "/" + MAX_FAVORITE_TITLES + " favorites", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Right-click a title above", NamedTextColor.YELLOW),
                                Component.text("to add or remove a favorite.", NamedTextColor.YELLOW)
                        )
                        .get(),
                Menu.ACTION_DO_NOTHING
        );

        for (int i = 0; i < FAVORITE_SLOTS.length; i++) {
            int slot = FAVORITE_SLOTS[i];
            if (i >= favoriteTitles.size()) {
                menu.setItem(
                        slot,
                        4,
                        new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                                .name(Component.text("Empty Favorite Slot", NamedTextColor.GRAY))
                                .lore(Component.text("Right-click a title above to favorite it.", NamedTextColor.DARK_GRAY))
                                .get(),
                        Menu.ACTION_DO_NOTHING
                );
                continue;
            }

            LegendaryTitles title = favoriteTitles.get(i);
            AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);
            ItemBuilder itemBuilder = new ItemBuilder(titledWeapon.generateItemStack(false));
            boolean equals = Objects.equals(weapon.getTitle(), title);
            boolean titleIsLocked = !weapon.getTitles().containsKey(title);

            if (equals) {
                itemBuilder.addLore(
                        Component.empty(),
                        Component.text("Selected", NamedTextColor.GREEN)
                );
                itemBuilder.enchant(Enchantment.RESPIRATION, 1);
            } else if (titleIsLocked) {
                itemBuilder.addLore(titledWeapon.getCostLore());
            } else {
                itemBuilder.addLore(
                        Component.empty(),
                        Component.text("Click to Select", NamedTextColor.GREEN)
                );
            }
            itemBuilder.addLore(
                    Component.empty(),
                    Component.text("★ Favorite", NamedTextColor.GOLD),
                    Component.text("Right-Click to Remove Favorite", NamedTextColor.RED)
            );

            menu.setItem(
                    slot,
                    4,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (e.isRightClick()) {
                            toggleFavoriteTitle(player, databasePlayer, weapon, titles, page, title);
                            return;
                        }
                        openTitleConfirmation(player, databasePlayer, weapon, titles, page, title);
                    }
            );
        }
    }

    private static void toggleFavoriteTitle(
            Player player,
            DatabasePlayer databasePlayer,
            AbstractLegendaryWeapon weapon,
            LegendaryTitles[] titles,
            int page,
            LegendaryTitles title
    ) {
        List<String> favoriteTitles = databasePlayer.getPveStats().getFavoriteWeaponTitles();
        String titleId = title.name();
        if (favoriteTitles.remove(titleId)) {
            player.sendMessage(Component.text("Removed ", NamedTextColor.GRAY)
                                        .append(Component.text(title.name, NamedTextColor.GOLD))
                                        .append(Component.text(" from your favorite titles.", NamedTextColor.GRAY))
            );
        } else {
            if (favoriteTitles.size() >= MAX_FAVORITE_TITLES) {
                player.sendMessage(Component.text("You can only have up to " + MAX_FAVORITE_TITLES + " favorite titles.", NamedTextColor.RED));
                return;
            }
            favoriteTitles.add(titleId);
            player.sendMessage(Component.text("Added ", NamedTextColor.GRAY)
                                        .append(Component.text(title.name, NamedTextColor.GOLD))
                                        .append(Component.text(" to your favorite titles.", NamedTextColor.GRAY))
            );
        }

        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1.2f);
        openWeaponTitleMenu(player, databasePlayer, weapon, titles, page);
    }

    private static LegendaryTitles getTitleById(String titleId) {
        if (titleId == null) {
            return null;
        }
        try {
            return LegendaryTitles.valueOf(titleId);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static void openTitleConfirmation(
            Player player,
            DatabasePlayer databasePlayer,
            AbstractLegendaryWeapon weapon,
            LegendaryTitles[] titles,
            int page,
            LegendaryTitles title
    ) {
        AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);
        boolean equals = Objects.equals(weapon.getTitle(), title);
        boolean titleIsLocked = !weapon.getTitles().containsKey(title);
        Set<Map.Entry<Currencies, Long>> cost = titledWeapon.getCost().entrySet();
        List<Component> loreCost = titledWeapon.getCostLore();

        if (equals) {
            player.sendMessage(Component.text("You already have this title on your weapon!", NamedTextColor.RED));
            return;
        }
        if (titleIsLocked) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            for (Map.Entry<Currencies, Long> currenciesLongEntry : cost) {
                Currencies currency = currenciesLongEntry.getKey();
                Long currencyCost = currenciesLongEntry.getValue();
                if (pveStats.getCurrencyValue(currency) < currencyCost) {
                    player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                .append(currency.getCostColoredName(currencyCost))
                                                .append(Component.text(" to apply this title!"))
                    );
                    return;
                }
            }
        }

        List<Component> confirmLore = new ArrayList<>();
        String titleName = titledWeapon.getTitleName();
        if (titleName.isEmpty()) {
            confirmLore.add(Component.text("Remove ", NamedTextColor.GRAY)
                                     .append(Component.text(weapon.getTitleName(), NamedTextColor.GREEN))
                                     .append(Component.text(" title"))
            );
        } else {
            confirmLore.add(Component.text("Apply ", NamedTextColor.GRAY)
                                     .append(Component.text(titleName, NamedTextColor.GREEN))
                                     .append(Component.text(" title"))
            );
        }
        if (titleIsLocked) {
            confirmLore.addAll(loreCost);
        }
        Menu.openConfirmationMenu(
                player,
                "Apply Title",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m2, e2) -> {
                    AbstractLegendaryWeapon newTitledWeapon = titleWeapon(player, databasePlayer, weapon, title);
                    openWeaponTitleMenu(player, databasePlayer, newTitledWeapon, titles, page);
                },
                (m2, e2) -> openWeaponTitleMenu(player, databasePlayer, weapon, titles, page),
                (m2) -> {
                }
        );
    }

    private static void openWeaponEditorAfterTick(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon) {
        new BukkitRunnable() {
            @Override
            public void run() {
                openWeaponEditor(player, databasePlayer, weapon);
            }
        }.runTaskLater(Warlords.getInstance(), 1);
    }

    public static AbstractLegendaryWeapon titleWeapon(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, LegendaryTitles title) {
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        boolean notPurchased = !weapon.getTitles().containsKey(title);
        AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);
        if (notPurchased) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            titledWeapon.getCost().forEach(pveStats::subtractCurrency);
            weapon.getTitles().put(title, new LegendaryWeaponTitleInfo());
        }
        weaponInventory.remove(weapon);
        weaponInventory.add(titledWeapon);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        player.sendMessage(Component.text("Titled Weapon: ", NamedTextColor.GRAY)
                                    .append(weapon.getHoverComponent(false))
                                    .append(Component.text(" and it became "))
                                    .append(titledWeapon.getHoverComponent(false))
                                    .append(Component.text("!"))
        );
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);

        if (notPurchased) {
            Bukkit.getPluginManager().callEvent(new WeaponTitlePurchaseEvent(player.getUniqueId(), weapon, title));
        }

        return titledWeapon;
    }

    public static void openWeaponTitleMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, int page) {
        openWeaponTitleMenu(player, databasePlayer, weapon, LegendaryTitles.VALUES, page);
    }

    public static void openWeaponTitleUpgradeMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon) {
        if (weapon == null) {
            return;
        }

        Menu menu = new Menu("Upgrade Weapon Title", 9 * 3);

        menu.setItem(2, 1,
                weapon.getUpgradedTitleItem(),
                (m, e) -> {
                    upgradeWeaponTitle(player, databasePlayer, weapon);
                    WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon);
                }
        );

        menu.setItem(4, 1,
                weapon.generateItemStack(false),
                (m, e) -> {
                }
        );

        menu.setItem(6, 1,
                new ItemBuilder(Material.RED_CONCRETE)
                        .name(Menu.DENY)
                        .lore(WeaponManagerMenu.GO_BACK)
                        .get(),
                (m, e) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon)
        );

        menu.openForPlayer(player);

    }

    public static void upgradeWeaponTitle(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon) {
        if (weapon == null) {
            return;
        }
        if (databasePlayer.getPveStats().getWeaponInventory().contains(weapon)) {
            LinkedHashMap<Spendable, Long> upgradeCost = weapon.getTitleUpgradeCost(weapon.getTitleLevelUpgraded());
            for (Map.Entry<Spendable, Long> currenciesLongEntry : upgradeCost.entrySet()) {
                currenciesLongEntry.getKey().subtractFromPlayer(databasePlayer, currenciesLongEntry.getValue());
            }
            weapon.upgradeTitleLevel();
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            player.sendMessage(Component.text("Upgraded Weapon Title: ", NamedTextColor.GRAY)
                                        .append(weapon.getHoverComponent(false))
            );
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
        }
    }


}

package com.ebicep.warlords.pve.journal;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.ACTION_DO_NOTHING;
import static com.ebicep.warlords.menu.Menu.MENU_BACK;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public final class DiscoveryJournalMenu {

    private static final int ITEMS_PER_ROW = 7;
    private static final int CONTENT_ROWS = 4;
    private static final int ITEMS_PER_PAGE = ITEMS_PER_ROW * CONTENT_ROWS;

    private DiscoveryJournalMenu() {
    }

    public static void open(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu("Discovery Journal", 9 * 4);

        menu.setItem(4, 0,
                new ItemBuilder(Material.KNOWLEDGE_BOOK)
                        .name(Component.text("Discovery Journal", NamedTextColor.AQUA))
                        .lore(WordWrap.wrap(Component.text("Track the enemies you have slain and every PvE resource.", NamedTextColor.GRAY), 160))
                        .get(),
                ACTION_DO_NOTHING
        );

        int discovered = 0;
        int total = 0;
        for (Mob.MobGroup group : MobDiscovery.JOURNAL_GROUPS) {
            total += group.mobs.length;
            discovered += MobDiscovery.discoveredCount(databasePlayer, group);
        }

        menu.setItem(2, 2,
                new ItemBuilder(Material.ZOMBIE_HEAD)
                        .name(Component.text("Mobs", NamedTextColor.GREEN))
                        .lore(
                                Component.text("Discovered ", NamedTextColor.GRAY)
                                         .append(Component.text(discovered + "/" + total, NamedTextColor.YELLOW)),
                                Component.empty(),
                                ComponentUtils.CLICK_TO_VIEW
                        )
                        .get(),
                (m, e) -> openMobs(player)
        );

        menu.setItem(6, 2,
                new ItemBuilder(Material.GOLD_NUGGET)
                        .name(Component.text("Resources", NamedTextColor.GOLD))
                        .lore(
                                WordWrap.wrap(Component.text("Every PvE resource and where it can be earned.", NamedTextColor.GRAY), 160)
                        )
                        .addLore(Component.empty(), ComponentUtils.CLICK_TO_VIEW)
                        .get(),
                (m, e) -> openResources(player)
        );

        menu.setItem(3, 3, WarlordsNewHotbarMenu.PvEMenu.MENU_BACK_PVE, (m, e) -> WarlordsNewHotbarMenu.PvEMenu.openPvEMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openMobs(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu("Mob Journal", 9 * 5);

        List<Mob.MobGroup> groups = MobDiscovery.JOURNAL_GROUPS;
        for (int i = 0; i < groups.size(); i++) {
            Mob.MobGroup group = groups.get(i);
            int discovered = MobDiscovery.discoveredCount(databasePlayer, group);
            menu.setItem(i % ITEMS_PER_ROW + 1, i / ITEMS_PER_ROW + 1,
                    new ItemBuilder(group.head)
                            .name(Component.text(group.name + " Mobs", group.textColor))
                            .lore(
                                    Component.text("Discovered ", NamedTextColor.GRAY)
                                             .append(Component.text(discovered + "/" + group.mobs.length, NamedTextColor.YELLOW)),
                                    Component.empty(),
                                    ComponentUtils.CLICK_TO_VIEW
                            )
                            .get(),
                    (m, e) -> openMobGroup(player, group, 1)
            );
        }

        menu.setItem(4, 4, MENU_BACK, (m, e) -> open(player));
        menu.openForPlayer(player);
    }

    public static void openMobGroup(Player player, Mob.MobGroup group, int page) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu(group.name + " Mobs", 9 * 6);
        Mob[] mobs = group.mobs;
        int start = (page - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, mobs.length);

        for (int i = start; i < end; i++) {
            int slot = i - start;
            menu.setItem(slot % ITEMS_PER_ROW + 1, slot / ITEMS_PER_ROW + 1, mobItem(databasePlayer, mobs[i]), ACTION_DO_NOTHING);
        }

        if (page > 1) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openMobGroup(player, group, page - 1)
            );
        }
        if (end < mobs.length) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openMobGroup(player, group, page + 1)
            );
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openMobs(player));
        menu.openForPlayer(player);
    }

    public static void openResources(Player player) {
        Menu menu = new Menu("Resource Journal", 9 * 4);

        ResourceDiscovery.Category[] categories = ResourceDiscovery.Category.VALUES;
        for (int i = 0; i < categories.length; i++) {
            ResourceDiscovery.Category category = categories[i];
            int count = ResourceDiscovery.entries(category).size();
            menu.setItem(i + 1, 1,
                    new ItemBuilder(category.icon)
                            .name(Component.text(category.displayName, category.textColor))
                            .lore(
                                    Component.text(count + " resources", NamedTextColor.GRAY),
                                    Component.empty(),
                                    ComponentUtils.CLICK_TO_VIEW
                            )
                            .get(),
                    (m, e) -> openResourceCategory(player, category, 1)
            );
        }

        menu.setItem(4, 3, MENU_BACK, (m, e) -> open(player));
        menu.openForPlayer(player);
    }

    public static void openResourceCategory(Player player, ResourceDiscovery.Category category, int page) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu(category.displayName, 9 * 6);
        List<ResourceDiscovery.Entry> entries = ResourceDiscovery.entries(category);
        int start = (page - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, entries.size());

        for (int i = start; i < end; i++) {
            int slot = i - start;
            menu.setItem(slot % ITEMS_PER_ROW + 1, slot / ITEMS_PER_ROW + 1, resourceItem(databasePlayer, entries.get(i)), ACTION_DO_NOTHING);
        }

        if (page > 1) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openResourceCategory(player, category, page - 1)
            );
        }
        if (end < entries.size()) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openResourceCategory(player, category, page + 1)
            );
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openResources(player));
        menu.openForPlayer(player);
    }

    private static ItemStack mobItem(DatabasePlayer databasePlayer, Mob mob) {
        boolean discovered = MobDiscovery.isDiscovered(databasePlayer, mob);
        if (!discovered) {
            return new ItemBuilder(Material.GRAY_DYE)
                    .name(Component.text("???", NamedTextColor.DARK_GRAY))
                    .lore(WordWrap.wrap(Component.text("Defeat this enemy at least once to unlock.", NamedTextColor.GRAY), 160))
                    .get();
        }

        String name = MobDiscovery.getDisplayName(mob);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Health: ", NamedTextColor.GRAY)
                          .append(Component.text(NumberFormat.addCommaAndRound(mob.maxHealth), NamedTextColor.GREEN)));
        lore.add(Component.text("Walk Speed: ", NamedTextColor.GRAY)
                          .append(Component.text(NumberFormat.formatOptionalHundredths(mob.walkSpeed), NamedTextColor.GREEN)));
        lore.add(Component.text("Damage Resistance: ", NamedTextColor.GRAY)
                          .append(Component.text(NumberFormat.formatOptionalHundredths(mob.damageResistance), NamedTextColor.GREEN)));
        lore.add(Component.text("Melee Damage: ", NamedTextColor.GRAY)
                          .append(Component.text(
                                  NumberFormat.addCommaAndRound(mob.minMeleeDamage) + " - " + NumberFormat.addCommaAndRound(mob.maxMeleeDamage),
                                  NamedTextColor.GREEN
                          )));
        lore.add(Component.empty());
        lore.add(Component.text("Times slain: ", NamedTextColor.GRAY)
                          .append(Component.text(NumberFormat.addCommas(MobDiscovery.getKills(databasePlayer, mob)), NamedTextColor.YELLOW)));

        String mechanics = MobDiscovery.getMechanics(mob);
        if (mechanics != null && !mechanics.isEmpty()) {
            lore.add(Component.empty());
            lore.add(Component.text("Mechanics", NamedTextColor.AQUA));
            lore.addAll(WordWrap.wrap(Component.text(mechanics, NamedTextColor.GRAY), 160));
        }

        return new ItemBuilder(mob.getHead())
                .name(Component.text(name, NamedTextColor.GREEN))
                .lore(lore)
                .get();
    }

    private static ItemStack resourceItem(DatabasePlayer databasePlayer, ResourceDiscovery.Entry entry) {
        List<Component> lore = new ArrayList<>();
        Long owned = entry.spendable().getFromPlayer(databasePlayer);
        if (owned != null) {
            lore.add(Component.text("Owned: ", NamedTextColor.GRAY)
                              .append(Component.text(NumberFormat.addCommas(owned), NamedTextColor.YELLOW)));
            lore.add(Component.empty());
        }
        lore.add(Component.text("Can be earned from:", NamedTextColor.AQUA));
        for (String source : entry.sources()) {
            lore.add(Component.text(" - ", NamedTextColor.DARK_GRAY).append(Component.text(source, NamedTextColor.GRAY)));
        }

        return new ItemBuilder(entry.spendable().getItem())
                .name(Component.text(entry.spendable().getName(), entry.spendable().getTextColor()))
                .lore(lore)
                .get();
    }
}

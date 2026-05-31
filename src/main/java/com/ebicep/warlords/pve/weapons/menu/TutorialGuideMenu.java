package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TutorialGuideMenu {

    public static void openMainMenu(Player player) {
        Menu menu = new Menu("Tutorial Guide", 9 * 5);

        menu.setItem(4, 0,
                item(
                        Material.KNOWLEDGE_BOOK,
                        Component.text("Tutorial Guide", NamedTextColor.AQUA),
                        List.of(
                                Component.text("Pick a topic to learn the basics.", NamedTextColor.GRAY),
                                Component.text("Short, practical explanations only.", NamedTextColor.DARK_GRAY)
                        )
                ),
                Menu.ACTION_DO_NOTHING
        );

        menu.setItem(2, 2,
                item(
                        Material.ZOMBIE_HEAD,
                        Component.text("Wave Defense", NamedTextColor.GREEN),
                        List.of(
                                Component.text("PvE survival mode.", NamedTextColor.GRAY),
                                Component.text("Fight waves, survive bosses, earn rewards.", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Click to view.", NamedTextColor.YELLOW)
                        )
                ),
                (m, e) -> openWaveDefenseMenu(player)
        );

        menu.setItem(4, 2,
                item(
                        Material.NETHERITE_SWORD,
                        Component.text("Onslaught", NamedTextColor.RED),
                        List.of(
                                Component.text("Fast-paced PvE pressure mode.", NamedTextColor.GRAY),
                                Component.text("Enemies scale quickly.", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Click to view.", NamedTextColor.YELLOW)
                        )
                ),
                (m, e) -> openOnslaughtMenu(player)
        );

        menu.setItem(6, 2,
                item(
                        Material.DIAMOND_SWORD,
                        Component.text("Weapons & Titles", NamedTextColor.GOLD),
                        List.of(
                                Component.text("Weapon rarities, legendary crafting,", NamedTextColor.GRAY),
                                Component.text("and passive Legendary Titles.", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Click to view.", NamedTextColor.YELLOW)
                        )
                ),
                (m, e) -> openWeaponsAndTitlesMenu(player)
        );

        menu.setItem(2, 3,
                item(
                        Material.NETHER_STAR,
                        Component.text("Insignia Upgrades", NamedTextColor.LIGHT_PURPLE),
                        List.of(
                                Component.text("In-game upgrade system.", NamedTextColor.GRAY),
                                Component.text("Spend Insignia during PvE runs.", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Click to view.", NamedTextColor.YELLOW)
                        )
                ),
                (m, e) -> openInsigniaMenu(player)
        );

        menu.setItem(4, 3,
                item(
                        Material.AMETHYST_SHARD,
                        Component.text("Star Pieces", NamedTextColor.AQUA),
                        List.of(
                                Component.text("Weapon progression currency.", NamedTextColor.GRAY),
                                Component.text("Can be synthesized into higher tiers.", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Click to view.", NamedTextColor.YELLOW)
                        )
                ),
                (m, e) -> openStarPiecesMenu(player)
        );

        menu.setItem(6, 3,
                item(
                        Material.CHEST,
                        Component.text("Materials", NamedTextColor.YELLOW),
                        List.of(
                                Component.text("Coins, shards, fragments,", NamedTextColor.GRAY),
                                Component.text("and supply drop tokens.", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Click to view.", NamedTextColor.YELLOW)
                        )
                ),
                (m, e) -> openMaterialsMenu(player)
        );

        menu.setItem(4, 4, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.fillEmptySlots(Menu.GRAY_EMPTY_PANE, Menu.ACTION_DO_NOTHING);
        menu.openForPlayer(player);
    }

    private static void openWaveDefenseMenu(Player player) {
        openInfoMenu(
                player,
                "Wave Defense",
                Material.ZOMBIE_HEAD,
                Component.text("Goal", NamedTextColor.YELLOW),
                Component.text("Survive waves of enemies.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("How it plays", NamedTextColor.YELLOW),
                Component.text("Each wave spawns mobs. Later waves become harder.", NamedTextColor.GRAY),
                Component.text("Boss waves test damage, healing, and positioning.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Rewards", NamedTextColor.YELLOW),
                Component.text("Higher waves and higher difficulty give better rewards.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Tip", NamedTextColor.YELLOW),
                Component.text("Use Insignia upgrades during the run.", NamedTextColor.GRAY)
        );
    }

    private static void openOnslaughtMenu(Player player) {
        openInfoMenu(
                player,
                "Onslaught",
                Material.NETHERITE_SWORD,
                Component.text("Goal", NamedTextColor.YELLOW),
                Component.text("Survive intense enemy pressure.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("How it plays", NamedTextColor.YELLOW),
                Component.text("Faster pacing than Wave Defense.", NamedTextColor.GRAY),
                Component.text("Enemy pressure ramps quickly.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Rewards", NamedTextColor.YELLOW),
                Component.text("Complete runs for PvE rewards and progression.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Tip", NamedTextColor.YELLOW),
                Component.text("Bring damage, sustain, and crowd control.", NamedTextColor.GRAY)
        );
    }

    private static void openWeaponsAndTitlesMenu(Player player) {
        openInfoMenu(
                player,
                "Weapons & Titles",
                Material.DIAMOND_SWORD,
                Component.text("Other rarity weapons", NamedTextColor.YELLOW),
                Component.text("Obtained from PvE drops and rewards.", NamedTextColor.GRAY),
                Component.text("Manage them at the Weaponsmith.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Legendary weapons", NamedTextColor.YELLOW),
                Component.text("Crafted through the Richard the Witchard the Weapon Specialist.", NamedTextColor.GRAY),
                Component.text("Require materials, currencies, and progression items.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Legendary Titles", NamedTextColor.YELLOW),
                Component.text("Passive effects attached to Legendary weapons.", NamedTextColor.GRAY),
                Component.text("Crafted or unlocked with title resources/tokens.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Tip", NamedTextColor.YELLOW),
                Component.text("Bind your best weapon to the class you play most.", NamedTextColor.GRAY)
        );
    }

    private static void openInsigniaMenu(Player player) {
        openInfoMenu(
                player,
                "Insignia Upgrades",
                Material.NETHER_STAR,
                Component.text("What they are", NamedTextColor.YELLOW),
                Component.text("Insignia are used inside PvE runs.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("How to use them", NamedTextColor.YELLOW),
                Component.text("Open the in-game upgrade menu.", NamedTextColor.GRAY),
                Component.text("Buy class, ability, or stat upgrades.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Why it matters", NamedTextColor.YELLOW),
                Component.text("Good upgrades make later waves much easier.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Tip", NamedTextColor.YELLOW),
                Component.text("Upgrade around your role: damage, tanking, or healing.", NamedTextColor.GRAY)
        );
    }

    private static void openStarPiecesMenu(Player player) {
        openInfoMenu(
                player,
                "Star Pieces",
                Material.AMETHYST_SHARD,
                Component.text("What they are", NamedTextColor.YELLOW),
                Component.text("Star Pieces are PvE progression items.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("How to get them", NamedTextColor.YELLOW),
                Component.text("Earn them through PvE rewards and drops.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Synthesis", NamedTextColor.YELLOW),
                Component.text("Combine lower tiers into higher tiers.", NamedTextColor.GRAY),
                Component.text("Use the Star Piece Synthesizer NPC.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Current tiers", NamedTextColor.YELLOW),
                Component.text("Common, Rare, Epic, Legendary, Ascendant.", NamedTextColor.GRAY)
        );
    }

    private static void openMaterialsMenu(Player player) {
        openInfoMenu(
                player,
                "Materials",
                Material.CHEST,
                Component.text("Coins", NamedTextColor.YELLOW),
                Component.text("General PvE currency.", NamedTextColor.GRAY),
                Component.text("Used for upgrades, crafting, synthesis, and costs.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Synthetic Shards", NamedTextColor.YELLOW),
                Component.text("Common crafting material.", NamedTextColor.GRAY),
                Component.text("Mostly used around weapon crafting/progression.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Legend Fragments", NamedTextColor.YELLOW),
                Component.text("Legendary crafting material.", NamedTextColor.GRAY),
                Component.text("Mostly used for Legendary weapons and titles.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Supply Drop Tokens", NamedTextColor.YELLOW),
                Component.text("Used at the Supply Drop NPC.", NamedTextColor.GRAY),
                Component.text("Trade them for rotating rewards or useful items.", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Tip", NamedTextColor.YELLOW),
                Component.text("Check NPC menus to see exact current costs.", NamedTextColor.GRAY)
        );
    }

    private static void openInfoMenu(Player player, String title, Material material, Component... lore) {
        Menu menu = new Menu(title, 9 * 5);

        menu.setItem(4, 1,
                item(
                        material,
                        Component.text(title, NamedTextColor.AQUA),
                        List.of(lore)
                ),
                Menu.ACTION_DO_NOTHING
        );

        menu.setItem(3, 4, Menu.MENU_BACK, (m, e) -> openMainMenu(player));
        menu.setItem(5, 4, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.fillEmptySlots(Menu.GRAY_EMPTY_PANE, Menu.ACTION_DO_NOTHING);
        menu.openForPlayer(player);
    }

    private static ItemStack item(Material material, Component name, List<Component> lore) {
        return new ItemBuilder(material)
                .name(name)
                .lore(lore)
                .get();
    }

}

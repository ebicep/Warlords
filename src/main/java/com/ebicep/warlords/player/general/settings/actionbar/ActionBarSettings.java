package com.ebicep.warlords.player.general.settings.actionbar;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class ActionBarSettings {

    public static final ItemStack ITEM = new ItemBuilder(Material.ANVIL)
            .name(Component.text("Action Bar Settings", NamedTextColor.GREEN))
            .lore(WordWrap.wrap(Component.text("Customize your action bar to display the information you want.", NamedTextColor.GRAY), 150))
            .get();

    public static void openMenu(Player player, DatabasePlayer databasePlayer) {
        Menu menu = new Menu("Action Bar Settings", 9 * 4);
        ActionBarSettings actionBarSettings = databasePlayer.getActionBarSettings();
        Category[] categories = new Category[]{
                actionBarSettings.healthCategory,
                actionBarSettings.gameCategory,
                actionBarSettings.cooldownCategory
        };
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            menu.setItem(i + 1, 1,
                    new ItemBuilder(categories[i].getItem())
                            .name(category.getName(databasePlayer))
                            .lore(category.getDescription(databasePlayer))
                            .get(),
                    (m, e) -> {
                        openCategoryMenu(player, databasePlayer, category);
                    }
            );
        }
        menu.setItem(3, 3, Menu.MENU_BACK, (m, e) -> WarlordsNewHotbarMenu.SettingsMenu.openSettingsMenu(player));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static void openCategoryMenu(Player player, DatabasePlayer databasePlayer, Category category) {
        Menu menu2 = new Menu(category.getName(databasePlayer).content(), 9 * 4);
        category.editMenu(player, databasePlayer, menu2);
        menu2.setItem(4, 3, Menu.MENU_BACK, (m2, e2) -> openMenu(player, databasePlayer));
        menu2.openForPlayer(player);
    }

    @Field("health")
    private HealthCategory healthCategory = new HealthCategory();
    @Field("game")
    private GameCategory gameCategory = new GameCategory();
    @Field("cooldown")
    private CooldownCategory cooldownCategory = new CooldownCategory();

    public HealthCategory getHealthCategory() {
        return healthCategory;
    }

    public GameCategory getGameCategory() {
        return gameCategory;
    }

    public CooldownCategory getCooldownCategory() {
        return cooldownCategory;
    }

    private interface Category {

        default void addToMenu(Player player, DatabasePlayer databasePlayer, Menu menu, List<CategoryItem> categoryItems) {
            for (int i = 0; i < categoryItems.size(); i++) {
                CategoryItem categoryItem = categoryItems.get(i);
                menu.setItem(i + 1, 1, categoryItem.getItem(), (m, e) -> {
                    categoryItem.onClick().accept(m, e);
                    openCategoryMenu(player, databasePlayer, this);
                });
            }
        }

        TextComponent getName(DatabasePlayer databasePlayer);

        List<Component> getDescription(DatabasePlayer databasePlayer);

        ItemStack getItem();

        void editMenu(Player player, DatabasePlayer databasePlayer, Menu menu);

    }

    public static class HealthCategory implements Category {

        private static final ItemStack ITEM = new ItemBuilder(Material.SPLASH_POTION, PotionType.INSTANT_HEAL).get();

        @Field("show_hp_text")
        private boolean showHPText = true;
        @Field("show_health")
        private boolean showHealth = true;
        @Field("show_max_health")
        private boolean showMaxHealth = true;

        public boolean isShowHPText() {
            return showHPText;
        }

        public boolean isShowHealth() {
            return showHealth;
        }

        public boolean isShowMaxHealth() {
            return showMaxHealth;
        }

        @Override
        public TextComponent getName(DatabasePlayer databasePlayer) {
            return Component.text("Health", NamedTextColor.GREEN);
        }

        @Override
        public List<Component> getDescription(DatabasePlayer databasePlayer) {
            return WordWrap.wrap(Component.text("Customize how your health is displayed.", NamedTextColor.GRAY), 140);
        }

        @Override
        public ItemStack getItem() {
            return ITEM;
        }

        @Override
        public void editMenu(Player player, DatabasePlayer databasePlayer, Menu menu) {
            ItemBuilder showHealthText = new ItemBuilder(Material.OAK_SIGN)
                    .name(Component.text("Show Health Text", NamedTextColor.GREEN))
                    .lore(Component.empty())
                    .addLore(WordWrap.wrap(Component.text("Toggles whether or not 'HP:' should be displayed.", NamedTextColor.GRAY), 140));
            if (showHPText && (!showHealth || !showMaxHealth)) {
                showHealthText.addLore(
                        Component.empty(),
                        Component.text("OVERRIDEN", NamedTextColor.RED)
                );
            }
            addToMenu(player, databasePlayer, menu, List.of(
                    new CategoryItem.BooleanCategoryItem(
                            showHealthText,
                            () -> showHPText,
                            b -> showHPText = b
                    ),
                    new CategoryItem.BooleanCategoryItem(
                            new ItemBuilder(Material.APPLE)
                                    .name(Component.text("Show Health Amount", NamedTextColor.GREEN))
                                    .lore(Component.empty())
                                    .addLore(WordWrap.wrap(Component.text("Toggles whether or not your current health should be displayed",
                                            NamedTextColor.GRAY
                                    ), 140)),
                            () -> showHealth,
                            b -> showHealth = b
                    ),
                    new CategoryItem.BooleanCategoryItem(
                            new ItemBuilder(Material.GOLDEN_APPLE)
                                    .name(Component.text("Show Max Health Amount", NamedTextColor.GREEN))
                                    .lore(Component.empty())
                                    .addLore(WordWrap.wrap(Component.text("Toggles whether or not your max health should be displayed", NamedTextColor.GRAY), 140)),
                            () -> showMaxHealth,
                            b -> showMaxHealth = b
                    )
            ));
        }

    }

    public static class GameCategory implements Category {

        private static final ItemStack ITEM = new ItemBuilder(Material.BOOK).get();

        @Field("show_team")
        private boolean showTeam = true;

        public boolean isShowTeam() {
            return showTeam;
        }

        @Override
        public TextComponent getName(DatabasePlayer databasePlayer) {
            return Component.text("Game", NamedTextColor.GREEN);
        }

        @Override
        public List<Component> getDescription(DatabasePlayer databasePlayer) {
            return WordWrap.wrap(Component.text("Customize how your game information is displayed.", NamedTextColor.GRAY), 140);
        }

        @Override
        public ItemStack getItem() {
            return ITEM;
        }

        @Override
        public void editMenu(Player player, DatabasePlayer databasePlayer, Menu menu) {
            addToMenu(player, databasePlayer, menu, List.of(
                    new CategoryItem.BooleanCategoryItem(
                            new ItemBuilder(Material.WHITE_BANNER)
                                    .name(Component.text("Show Team", NamedTextColor.GREEN))
                                    .lore(Component.empty())
                                    .addLore(WordWrap.wrap(Component.text("Toggles whether or not your team should be displayed.", NamedTextColor.GRAY), 140)),
                            () -> showTeam,
                            b -> showTeam = b
                    )
            ));
        }
    }

    public static class CooldownCategory implements Category {

        private static final ItemStack ITEM = new ItemBuilder(Material.CLOCK).get();

        @Field("show_cooldowns")
        private boolean showCooldowns = true;

        public boolean isShowCooldowns() {
            return showCooldowns;
        }

        @Override
        public TextComponent getName(DatabasePlayer databasePlayer) {
            return Component.text("Cooldown", NamedTextColor.GREEN);
        }

        @Override
        public List<Component> getDescription(DatabasePlayer databasePlayer) {
            return WordWrap.wrap(Component.text("Customize how your cooldowns are displayed.", NamedTextColor.GRAY), 140);
        }

        @Override
        public ItemStack getItem() {
            return ITEM;
        }

        @Override
        public void editMenu(Player player, DatabasePlayer databasePlayer, Menu menu) {
            addToMenu(player, databasePlayer, menu, List.of(
                    new CategoryItem.BooleanCategoryItem(
                            new ItemBuilder(Material.CLOCK)
                                    .name(Component.text("Show Cooldowns", NamedTextColor.GREEN))
                                    .lore(Component.empty())
                                    .lore(WordWrap.wrap(Component.text("Toggles whether or not your cooldowns should be displayed.", NamedTextColor.GRAY), 140)),
                            () -> showCooldowns,
                            b -> showCooldowns = b
                    )
            ));
        }
    }

}

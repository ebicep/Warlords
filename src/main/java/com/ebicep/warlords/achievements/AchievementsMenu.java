package com.ebicep.warlords.achievements;

import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.achievements.types.TieredAchievements;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;

public class AchievementsMenu {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("MM/dd/yyyy hh:mm")
            .withZone(ZoneId.of("America/New_York"));
    ;

    //GENERAL - CTF - TDM - GAMEMODE - GAMEMODE
    //TIERED ACHIEVEMENTS - CHALLENGES
    //ACHIEVEMENT HISTORY
    public static void openAchievementsMenu(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            Menu menu = new Menu("Achievements", 9 * 4);

            menu.setItem(
                    1,
                    1,
                    new ItemBuilder(Material.STONE_AXE)
                            .name(ChatColor.GREEN + "General")
                            .get(),
                    (m, e) -> openAchievementTypeMenu(player, databasePlayer, null)
            );

            int x = 0;
            for (GameMode gameMode : GameMode.VALUES) {
                if (gameMode.getItemStack() == null) {
                    continue;
                }
                menu.setItem(
                        x + 2,
                        1,
                        new ItemBuilder(gameMode.getItemStack())
                                .name(ChatColor.GREEN + gameMode.getName())
                                .get(),
                        (m, e) -> openAchievementTypeMenu(player, databasePlayer, gameMode)
                );
                x++;
            }

            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

    public static void openAchievementTypeMenu(Player player, DatabasePlayer databasePlayer, GameMode gameMode) {
        Menu menu = new Menu("Achievements - " + (gameMode == null ? "General" : gameMode.getName()), 9 * 4);

        menu.setItem(
                2,
                1,
                new ItemBuilder(Material.DIAMOND)
                        .name(ChatColor.GREEN + "Challenge Achievements")
                        .get(),
                (m, e) -> openAchievementsGameModeMenu(player,
                        databasePlayer,
                        gameMode,
                        "Challenge",
                        ChallengeAchievements.ChallengeAchievementRecord.class,
                        ChallengeAchievements.VALUES
                )
        );
        menu.setItem(
                6,
                1,
                new ItemBuilder(Material.DIAMOND_BLOCK)
                        .name(ChatColor.GREEN + "Tiered Achievements")
                        .get(),
                (m, e) -> openAchievementsGameModeMenu(player,
                        databasePlayer,
                        gameMode,
                        "Tiered",
                        TieredAchievements.TieredAchievementRecord.class,
                        TieredAchievements.TIERED_ACHIEVEMENTS_GROUPS.get(gameMode)
                )
        );

        menu.setItem(3, 3, MENU_BACK, (m, e) -> openAchievementsMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static <T extends Achievement.AbstractAchievementRecord<R>, R extends Enum<R> & Achievement> void openAchievementsGameModeMenu(
            Player player,
            DatabasePlayer databasePlayer,
            GameMode gameMode,
            String menuName,
            Class<T> recordClass,
            R[][] enumsValues
    ) {
        List<R> unlockedAchievements = databasePlayer.getAchievements().stream()
                .filter(recordClass::isInstance)
                .map(recordClass::cast)
                .map(Achievement.AbstractAchievementRecord::getAchievement)
                .collect(Collectors.toList());

        Menu menu = new Menu((gameMode == null ? "General" : gameMode.getName() + " - " + menuName), 9 * 6);
        int x = 0;
        int y = 0;
        for (R[] achievements : enumsValues) {
            for (R achievement : achievements) {
                if (achievement.getGameMode() != gameMode) {
                    continue;
                }
                boolean hasAchievement = unlockedAchievements.contains(achievement);
                boolean shouldObfuscate = !hasAchievement && achievement.isHidden();
                ItemBuilder itemBuilder = new ItemBuilder(hasAchievement ? Material.WATER_BUCKET : Material.BUCKET)
                        .name(ChatColor.GREEN.toString() + (shouldObfuscate ? ChatColor.MAGIC : "") + achievement.getName())
                        .flags(ItemFlag.HIDE_ENCHANTS);
                if (!achievement.getDescription().isEmpty()) {
                    itemBuilder.lore(
                            WordWrap.wrapWithNewline(
                                    ChatColor.GRAY.toString() + (shouldObfuscate ? ChatColor.MAGIC : "") + achievement.getDescription(),
                                    160
                            ));
                }
                itemBuilder.addLore(ChatColor.GREEN + (shouldObfuscate ?
                        ChatColor.MAGIC + "\nSpec:" + ChatColor.RESET + " " + ChatColor.GOLD + ChatColor.MAGIC + "hiddenSpec"
                        :
                        "\nSpec: " + ChatColor.GOLD + (achievement.getSpec() != null ? achievement.getSpec().name : "Any"))
                );
                if (achievement.getDifficulty() != null) {
                    itemBuilder.addLore(ChatColor.GREEN + (shouldObfuscate ?
                            ChatColor.MAGIC + "Difficulty:" + ChatColor.RESET + " " + ChatColor.GOLD + ChatColor.MAGIC + "difficulty"
                            :
                            "Difficulty: " + ChatColor.GOLD + achievement.getDifficulty().getColoredName())
                    );
                }
                if (hasAchievement) {
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                }
                menu.setItem(
                        x,
                        y,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (hasAchievement) {
                                openAchievementHistoryMenu(player,
                                        databasePlayer, recordClass,
                                        achievement,
                                        (m2, e2) -> openAchievementsGameModeMenu(player, databasePlayer, gameMode, menuName,
                                                recordClass, enumsValues
                                        ),
                                        1
                                );
                            }
                        }
                );
                y++;
            }
            x++;
            y = 0;
            if (x == 9) {
                //TODO PAGE
                x = 0;

            }
        }


        menu.setItem(3, 5, MENU_BACK, (m, e) -> openAchievementTypeMenu(player, databasePlayer, gameMode));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static <T extends Achievement.AbstractAchievementRecord<R>, R extends Enum<R> & Achievement> void openAchievementsGameModeMenu(
            Player player,
            DatabasePlayer databasePlayer,
            GameMode gameMode,
            String menuName,
            Class<T> recordClass,
            R[] enumsValues
    ) {
        List<R> unlockedAchievements = databasePlayer.getAchievements().stream()
                .filter(recordClass::isInstance)
                .map(recordClass::cast)
                .map(Achievement.AbstractAchievementRecord::getAchievement)
                .collect(Collectors.toList());

        Menu menu = new Menu((gameMode == null ? "General" : gameMode.getName() + " - " + menuName), 9 * 6);
        int x = 0;
        int y = 0;
        for (R achievement : enumsValues) {
            if (achievement.getGameMode() != gameMode) {
                continue;
            }
            boolean hasAchievement = unlockedAchievements.contains(achievement);
            boolean shouldObfuscate = !hasAchievement && achievement.isHidden();
            ItemBuilder itemBuilder = new ItemBuilder(hasAchievement ? Material.WATER_BUCKET : Material.BUCKET)
                    .name(ChatColor.GREEN.toString() + (shouldObfuscate ? ChatColor.MAGIC : "") + achievement.getName())
                    .flags(ItemFlag.HIDE_ENCHANTS);
            if (!achievement.getDescription().isEmpty()) {
                itemBuilder.lore(
                        WordWrap.wrapWithNewline(
                                ChatColor.GRAY.toString() + (shouldObfuscate ? ChatColor.MAGIC : "") + achievement.getDescription(),
                                160
                        ));
            }
            itemBuilder.addLore(ChatColor.GREEN + (shouldObfuscate ?
                    ChatColor.MAGIC + "\nSpec:" + ChatColor.RESET + " " + ChatColor.GOLD + ChatColor.MAGIC + "hiddenSpec"
                    :
                    "\nSpec: " + ChatColor.GOLD + (achievement.getSpec() != null ? achievement.getSpec().name : "Any")));
            if (achievement.getDifficulty() != null) {
                itemBuilder.addLore(ChatColor.GREEN + (shouldObfuscate ?
                        ChatColor.MAGIC + "Difficulty:" + ChatColor.RESET + " " + ChatColor.GOLD + ChatColor.MAGIC + "difficulty"
                        :
                        "Difficulty: " + ChatColor.GOLD + achievement.getDifficulty().getColoredName())
                );
            }
            if (hasAchievement) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            menu.setItem(
                    x,
                    y,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (hasAchievement) {
                            openAchievementHistoryMenu(player,
                                    databasePlayer, recordClass,
                                    achievement,
                                    (m2, e2) -> openAchievementsGameModeMenu(player, databasePlayer, gameMode, menuName,
                                            recordClass, enumsValues
                                    ),
                                    1
                            );
                        }
                    }
            );

            x++;
            if (x == 9) {
                x = 0;
                y++;
            }
        }


        menu.setItem(3, 5, MENU_BACK, (m, e) -> openAchievementTypeMenu(player, databasePlayer, gameMode));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static <T extends Achievement.AbstractAchievementRecord<R>, R extends Enum<R> & Achievement> void openAchievementHistoryMenu(
            Player player,
            DatabasePlayer databasePlayer,
            Class<T> recordClass,
            R achievement,
            BiConsumer<Menu, InventoryClickEvent> menuBack,
            int page
    ) {
        List<T> achievementRecords = databasePlayer.getAchievements().stream()
                                                   .filter(recordClass::isInstance)
                                                   .map(recordClass::cast)
                                                   .filter(t -> t.getAchievement() == achievement)
                                                   .collect(Collectors.toList());

        Menu menu = new Menu("Achievement History ", 9 * 6);

        for (int i = 0; i < 45; i++) {
            int achievementIndex = ((page - 1) * 45) + i;
            if (achievementIndex >= achievementRecords.size()) {
                break;
            }
            T achievementRecord = achievementRecords.get(achievementIndex);

            menu.setItem(
                    i % 9,
                    i / 9,
                    new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + achievement.getName())
                            .lore(ChatColor.GRAY + DATE_FORMAT.format(achievementRecord.getDate()))
                            .get(),
                    (m, e) -> {
                    }
            );
        }
        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> {
                        openAchievementHistoryMenu(player, databasePlayer, recordClass, achievement, menuBack, page - 1);
                    }
            );
        }
        if (achievementRecords.size() > (page * 45)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> {
                        openAchievementHistoryMenu(player, databasePlayer, recordClass, achievement, menuBack, page + 1);
                    }
            );
        }

        menu.setItem(3, 5, MENU_BACK, menuBack);
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}

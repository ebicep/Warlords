package com.ebicep.warlords.achievements;

import com.ebicep.warlords.Warlords;
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
import org.bukkit.inventory.ItemFlag;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;

public class AchievementsMenu {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh.mm aa");

    //GENERAL - CTF - TDM - GAMEMODE - GAMEMODE
    //TIERED ACHIEVEMENTS - CHALLENGES
    //ACHIEVEMENT HISTORY
    public static void openAchievementsMenu(Player player) {
        Menu menu = new Menu("Achievements", 9 * 6);

        menu.setItem(
                1,
                1,
                new ItemBuilder(Material.STONE_AXE)
                        .name(ChatColor.GREEN + "General")
                        .get(),
                (m, e) -> openAchievementTypeMenu(player, null)
        );
        for (int i = 0; i < GameMode.values().length; i++) {
            GameMode gameMode = GameMode.values()[i];
            if (gameMode.itemStack == null) continue;
            menu.setItem(
                    i + 1,
                    1,
                    new ItemBuilder(gameMode.itemStack)
                            .name(ChatColor.GREEN + gameMode.name)
                            .get(),
                    (m, e) -> openAchievementTypeMenu(player, gameMode)
            );
        }

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openAchievementTypeMenu(Player player, GameMode gameMode) {
        Menu menu = new Menu("Achievements - " + (gameMode == null ? "General" : gameMode.name), 9 * 4);

        menu.setItem(
                2,
                1,
                new ItemBuilder(Material.DIAMOND)
                        .name(ChatColor.GREEN + "Challenge Achievements")
                        .get(),
                (m, e) -> openChallengeAchievementsMenu(player, gameMode)
        );
        menu.setItem(
                6,
                1,
                new ItemBuilder(Material.DIAMOND_BLOCK)
                        .name(ChatColor.GREEN + "Tiered Achievements")
                        .get(),
                (m, e) -> {
                    openTieredAchievementsMenu(player, gameMode);
                }
        );

        menu.setItem(3, 3, MENU_BACK, (m, e) -> openAchievementsMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openChallengeAchievementsMenu(Player player, GameMode gameMode) {
        if (DatabaseManager.playerService == null) return;
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        List<ChallengeAchievements> achievementRecords = databasePlayer.getAchievements().stream()
                .filter(abstractAchievementRecord -> abstractAchievementRecord.getAchievement() instanceof ChallengeAchievements)
                .map(Achievement.AbstractAchievementRecord::getAchievement)
                .map(ChallengeAchievements.class::cast)
                .collect(Collectors.toList());
        List<ChallengeAchievements> challengeAchievements = Arrays.stream(ChallengeAchievements.values())
                .filter(achievements -> achievements.gameMode == gameMode)
                .collect(Collectors.toList());

        Menu menu = new Menu("Challenge Achievements", 9 * 6);

        int x = 0;
        int y = 0;
        for (ChallengeAchievements achievement : challengeAchievements) {
            boolean hasAchievement = achievementRecords.stream().anyMatch(achievements -> achievements == achievement);
            ItemBuilder itemBuilder = new ItemBuilder(hasAchievement ? Warlords.getHead(UUID.fromString("9f2b2230-3b2c-4b0f-a141-d7b598e236c7")) : Warlords.getHead(UUID.fromString("70b6981a-6ae8-4e76-8aeb-0fcd510f4be7")))
                    .name(ChatColor.GREEN + achievement.name + " - " + (achievement.spec == null ? "General" : achievement.spec.name))
                    .lore(ChatColor.WHITE + WordWrap.wrapWithNewline(achievement.description, 200) +
                            (hasAchievement ? "\n\n" + ChatColor.GREEN + "Unlocked!" : ""))
                    .flags(ItemFlag.HIDE_ENCHANTS);
            if (hasAchievement) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            menu.setItem(
                    x,
                    y,
                    itemBuilder.get(),
                    (m, e) -> openChallengeAchievementHistoryMenu(player, gameMode, achievement)
            );
            x++;
            if (x == 9) {
                x = 0;
                y++;
            }
        }


        menu.setItem(3, 5, MENU_BACK, (m, e) -> openAchievementsMenu(player));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openChallengeAchievementHistoryMenu(Player player, GameMode gameMode, ChallengeAchievements achievement) {
        if (DatabaseManager.playerService == null) return;
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        List<ChallengeAchievements.ChallengeAchievementRecord> achievementRecords = databasePlayer.getAchievements().stream()
                .filter(ChallengeAchievements.ChallengeAchievementRecord.class::isInstance)
                .map(ChallengeAchievements.ChallengeAchievementRecord.class::cast)
                .filter(t -> t.getAchievement() == achievement)
                .collect(Collectors.toList());

        Menu menu = new Menu("Achievement History", 9 * 6);

        int x = 0;
        int y = 0;
        for (ChallengeAchievements.ChallengeAchievementRecord achievementRecord : achievementRecords) {
            menu.setItem(
                    x,
                    y,
                    new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + achievement.name)
                            .lore(ChatColor.GRAY + DATE_FORMAT.format(achievementRecord.getDate()))
                            .get(),
                    (m, e) -> {
                    }
            );

            x++;
            if (x == 9) {
                x = 0;
                y++;
            }
        }

        menu.setItem(3, 5, MENU_BACK, (m, e) -> openChallengeAchievementsMenu(player, gameMode));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openTieredAchievementsMenu(Player player, GameMode gameMode) {
        if (DatabaseManager.playerService == null) return;
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        List<TieredAchievements> achievementRecords = databasePlayer.getAchievements().stream()
                .filter(abstractAchievementRecord -> abstractAchievementRecord.getAchievement() instanceof TieredAchievements)
                .map(Achievement.AbstractAchievementRecord::getAchievement)
                .map(TieredAchievements.class::cast)
                .collect(Collectors.toList());
        List<TieredAchievements> tieredAchievements = Arrays.stream(TieredAchievements.values())
                .filter(achievements -> achievements.gameMode == gameMode)
                .collect(Collectors.toList());

        Menu menu = new Menu("Challenge Achievements - " + gameMode.name, 9 * 6);

        int x = 0;
        int y = 0;
        for (TieredAchievements achievement : tieredAchievements) {
            boolean hasAchievement = achievementRecords.stream().anyMatch(achievements -> achievements == achievement);
            ItemBuilder itemBuilder = new ItemBuilder(hasAchievement ? Warlords.getHead(UUID.fromString("9f2b2230-3b2c-4b0f-a141-d7b598e236c7")) : Warlords.getHead(UUID.fromString("70b6981a-6ae8-4e76-8aeb-0fcd510f4be7")))
                    .name(ChatColor.GREEN + achievement.name)// + " - " + (achievement.spec.name == null ? "General" : achievement.spec.name))
                    .lore(ChatColor.WHITE + WordWrap.wrapWithNewline(achievement.description, 200) +
                            (hasAchievement ? "\n\n" + ChatColor.GREEN + "Unlocked!" : ""))
                    .flags(ItemFlag.HIDE_ENCHANTS);
            if (hasAchievement) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            menu.setItem(
                    x,
                    y,
                    itemBuilder.get(),
                    (m, e) -> openTieredAchievementHistoryMenu(player, gameMode, achievement)
            );
            x++;
            if (x == 9) {
                x = 0;
                y++;
            }
        }


        menu.setItem(3, 5, MENU_BACK, (m, e) -> openAchievementsMenu(player));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openTieredAchievementHistoryMenu(Player player, GameMode gameMode, TieredAchievements achievement) {
        if (DatabaseManager.playerService == null) return;
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        List<TieredAchievements.TieredAchievementRecord> achievementRecords = databasePlayer.getAchievements().stream()
                .filter(TieredAchievements.TieredAchievementRecord.class::isInstance)
                .map(TieredAchievements.TieredAchievementRecord.class::cast)
                .filter(t -> t.getAchievement() == achievement)
                .collect(Collectors.toList());

        Menu menu = new Menu("Achievement History", 9 * 6);

        int x = 0;
        int y = 0;
        for (TieredAchievements.TieredAchievementRecord achievementRecord : achievementRecords) {

            menu.setItem(
                    x,
                    y,
                    new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + achievement.name)
                            .lore(ChatColor.GRAY + DATE_FORMAT.format(achievementRecord.getDate()))
                            .get(),
                    (m, e) -> {
                    }
            );

            x++;
            if (x == 9) {
                x = 0;
                y++;
            }
        }

        menu.setItem(3, 5, MENU_BACK, (m, e) -> openTieredAchievementsMenu(player, gameMode));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

//    public static <T extends Achievement.AbstractAchievementRecord<R>, R extends Enum<R>> void openChallengeAchievementsMenu(
//            Player player,
//            GameMode gameMode,
//            Class<T> recordClass,
//            Class<R> rd
//    ) {
//        if (DatabaseManager.playerService == null) return;
//        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
//        List<R> unlockedAchievements = databasePlayer.getAchievements().stream()
//                .filter(recordClass::isInstance)
//                .map(recordClass::cast)
//                .map(Achievement.AbstractAchievementRecord::getAchievement)
//                .collect(Collectors.toList());
//        List<T> challengeAchievements = Arrays.stream(rd.getEnumConstants())
//                    .filter(r -> r.getGameMode() == null || r.getGameMode() == gameMode)
//                    .sorted(Comparator.nullsFirst(Comparator.comparing(Achievement.AbstractAchievementRecord::getGameMode)))
//                    .collect(Collectors.toList());
//
//        Menu menu = new Menu("Challenge Achievements - " + gameMode.name, 9 * 6);
//        int x = 0;
//        int y = 0;
//        for (T achievement : challengeAchievements) {
//            ItemBuilder itemBuilder = new ItemBuilder(Material.DIAMOND)
//                    .name(ChatColor.GREEN + achievement.getName() + " - " + (achievement.getGameMode() == null ? "General" : achievement.getGameMode()))
//                    .lore(WordWrap.wrapWithNewline(achievement.getDescription(), 200))
//                    .flags(ItemFlag.HIDE_ENCHANTS);
//            if (unlockedAchievements.stream().anyMatch(r -> r == achievement.getAchievement())) {
//                itemBuilder.enchant(Enchantment.OXYGEN, 1);
//            }
//            menu.setItem(
//                    x,
//                    y,
//                    itemBuilder.get(),
//                    (m, e) -> {
//                        openAchievementHistoryMenu(player, gameMode, recordClass, achievement);
//                    }
//            );
//            x++;
//            if (x == 8) {
//                x = 0;
//                y++;
//            }
//        }
//
//
//        menu.setItem(3, 5, MENU_BACK, (m, e) -> openAchievementTypeMenu(player, gameMode));
//        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
//        menu.openForPlayer(player);
//    }
//
//    public static <T extends Achievement.AbstractAchievementRecord<R>, R extends Enum<R>> void openAchievementHistoryMenu(
//            Player player,
//            GameMode gameMode,
//            Class<T> recordClass,
//            T achievement
//    ) {
//        if (DatabaseManager.playerService == null) return;
//        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
//        List<T> achievementRecords = databasePlayer.getAchievements().stream()
//                .filter(recordClass::isInstance)
//                .map(recordClass::cast)
//                .filter(t -> t.getAchievement() == achievement.getAchievement())
//                .collect(Collectors.toList());
//
//        Menu menu = new Menu("Achievement History ", 9 * 6);
//
//        int x = 0;
//        int y = 0;
//        for (T achievementRecord : achievementRecords) {
//
//            menu.setItem(
//                    x,
//                    y,
//                    new ItemBuilder(Material.BOOK)
//                            .name(ChatColor.GREEN + achievement.getName())
//                            .lore(ChatColor.GRAY + DATE_FORMAT.format(achievementRecord.getDate()))
//                            .get(),
//                    (m, e) -> {}
//            );
//
//            x++;
//            if (x == 8) {
//                x = 0;
//                y++;
//            }
//        }
//
//        menu.setItem(3, 5, MENU_BACK, (m, e) -> openChallengeAchievementsMenu(player, gameMode, recordClass));
//        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
//        menu.openForPlayer(player);
//    }

}

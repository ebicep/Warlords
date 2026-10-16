package com.ebicep.warlords.game.option.pve.darkdescent;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DarkDescentMenu {

    public static void openDarkDescentMenu(Player player) {
        DarkDescentLeaderboard.loadCurrentWeek(fastest -> openDarkDescentMenu(player, fastest));
    }

    private static void openDarkDescentMenu(Player player, List<DatabasePlayer> fastest) {
        Menu menu = new Menu("Dark Descent", 9 * 6);
        DarkDescentProgress progress = DatabaseManager.getPlayer(player.getUniqueId()).getPveStats().getDarkDescentProgress();
        List<Pair<Spendable, Long>> weeklyRewards = DarkDescentChest.getWeeklyRewards();
        String timeTill = DateUtil.getTimeTill(DateUtil.getNextWeeklyResetDate(), true, true, true, false);

        menu.setItem(4, 0,
                new ItemBuilder(Material.CLOCK)
                        .name(Component.text("Dark Descent", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD))
                        .lore(
                                Component.text("Descend " + DarkDescentOption.FLOORS + " floors alone, each floor", NamedTextColor.GRAY),
                                Component.text("rewards its chest once per week.", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Attempts remaining: ", NamedTextColor.GRAY)
                                         .append(Component.text(progress.getAttemptsRemaining() + "/" + DarkDescentProgress.MAX_ATTEMPTS, NamedTextColor.YELLOW)),
                                Component.text("Resets in ", NamedTextColor.GRAY).append(Component.text(timeTill, NamedTextColor.YELLOW))
                        )
                        .get(),
                Menu.ACTION_DO_NOTHING
        );

        for (int floor = 1; floor <= DarkDescentOption.FLOORS; floor++) {
            Pair<Spendable, Long> reward = weeklyRewards.get(floor - 1);
            boolean earned = progress.hasEarned(floor);
            menu.setItem(getSlot(floor),
                    new ItemBuilder(earned ? Material.GREEN_WOOL : DarkDescentChest.forFloor(floor).getMaterial())
                            .name(Component.text("Floor " + floor, earned ? NamedTextColor.GREEN : NamedTextColor.DARK_PURPLE))
                            .lore(
                                    reward.getA().getCostColoredName(reward.getB()),
                                    Component.empty(),
                                    earned
                                    ? Component.text("Earned this week", NamedTextColor.GREEN)
                                    : Component.text("Clear floor " + floor + " to earn this chest", NamedTextColor.GRAY)
                            )
                            .get(),
                    Menu.ACTION_DO_NOTHING
            );
        }

        Component status = progress.isCompleted()
                           ? Component.text("You conquered Dark Descent this week!", NamedTextColor.GREEN)
                           : progress.canStart()
                             ? Component.text("Click to begin your descent", NamedTextColor.YELLOW)
                             : Component.text("You have no attempts remaining this week.", NamedTextColor.RED);
        menu.setItem(4, 5,
                new ItemBuilder(Material.SCULK_CATALYST)
                        .name(Component.text("Begin Descent", NamedTextColor.DARK_PURPLE))
                        .lore(status)
                        .get(),
                (m, e) -> {
                    if (!progress.canStart()) {
                        player.sendMessage(status);
                        return;
                    }
                    GameStartCommand.startGamePvE(player, GameMode.DARK_DESCENT, queueEntryBuilder -> {
                        queueEntryBuilder.setMap(GameMap.DARK_DESCENT);
                        queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                    });
                }
        );
        menu.setItem(0, 5, getLeaderboardItem(fastest, progress), Menu.ACTION_DO_NOTHING);
        menu.setItem(8, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static ItemStack getLeaderboardItem(List<DatabasePlayer> fastest, DarkDescentProgress progress) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Fastest completions this week", NamedTextColor.GRAY));
        lore.add(Component.empty());
        if (fastest.isEmpty()) {
            lore.add(Component.text("No one has conquered Dark Descent yet.", NamedTextColor.GRAY));
        }
        for (int i = 0; i < fastest.size(); i++) {
            DatabasePlayer databasePlayer = fastest.get(i);
            lore.add(Component.text("#" + (i + 1) + " ", NamedTextColor.YELLOW)
                              .append(Component.text(databasePlayer.getName(), NamedTextColor.AQUA))
                              .append(Component.text(" - ", NamedTextColor.GRAY))
                              .append(Component.text(DarkDescentLeaderboard.formatTime(databasePlayer.getPveStats()
                                                                                                     .getDarkDescentProgress()
                                                                                                     .getCompletionTicks()), NamedTextColor.GREEN)));
        }
        lore.add(Component.empty());
        lore.add(Component.text("Your time: ", NamedTextColor.GRAY)
                          .append(progress.isCompleted()
                                  ? Component.text(DarkDescentLeaderboard.formatTime(progress.getCompletionTicks()), NamedTextColor.GREEN)
                                  : Component.text("N/A", NamedTextColor.RED)));
        for (int place = 1; place <= DarkDescentLeaderboard.REWARDED_PLACES; place++) {
            lore.add(Component.empty());
            lore.add(Component.text("#" + place + " Weekly Rewards:", NamedTextColor.GOLD));
            DarkDescentLeaderboard.getRewards(place).forEach((spendable, amount) ->
                    lore.add(Component.text(" - ", NamedTextColor.GRAY).append(spendable.getCostColoredName(amount))));
        }
        return new ItemBuilder(Material.GOLD_BLOCK)
                .name(Component.text("Leaderboard", NamedTextColor.GOLD))
                .lore(lore)
                .get();
    }

    /**
     * Floors 1-9 and 11-19 fill rows 1 and 3, floors 10 and 20 sit centered in rows 2 and 4
     */
    private static int getSlot(int floor) {
        if (floor % 10 == 0) {
            return 4 + floor / 10 * 2 * 9;
        }
        int row = floor < 10 ? 1 : 3;
        return floor % 10 - 1 + row * 9;
    }
}

package com.ebicep.warlords.game.option.pve.darkdescent;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.database.DatabaseManager;
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

import java.util.List;

public class DarkDescentMenu {

    public static void openDarkDescentMenu(Player player) {
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
        menu.setItem(8, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
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

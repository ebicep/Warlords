package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AnomalyMenu {

    public static void openAnomalyMenu(Player player) {
        Menu menu = new Menu("Anomaly", 9 * 6);
        Anomalies currentAnomaly = AnomalyOption.getDailyAnomaly();
        List<Component> rewards = new ArrayList<>();

        for (Spendable spendable : currentAnomaly.getRewards().keySet()) {
            rewards.add(spendable.getCostColoredName(currentAnomaly.getRewards().get(spendable)));
        }

        String timeTill = DateUtil.getTimeTill(DateUtil.getNextResetDate(),
                true,
                true,
                true,
                false
        );

        menu.setItem(
                4, 1,
                new ItemBuilder(Material.BARRIER)
                        .name(Component.text("Next Anomaly appears in: ", NamedTextColor.RED).append(Component.text(timeTill, NamedTextColor.YELLOW)))
                        .get(),
                (m, e) -> {}
        );
        menu.setItem(
                4, 3,
                new ItemBuilder(Material.AMETHYST_BLOCK)
                        .name(Component.text(currentAnomaly.getName(), NamedTextColor.GOLD, TextDecoration.BOLD))
                        .lore(currentAnomaly.getDescription())
                        .addLore(List.of(
                                Component.empty(),
                                Component.text("Possible rewards:")
                        ))
                        .addLore(rewards)
                        .get(),
                (m, e) -> {
                    GameStartCommand.startGamePvE(player, GameMode.ANOMALY,queueEntryBuilder -> {
                        GameMap map;
                        map = switch (currentAnomaly) {
                            case ENDLESS_PARADOX -> GameMap.ENDLESS_PARADOX;
                            case OPEX_ANOMALY -> GameMap.ENDLESS_PARADOX;
                            case WHAT_ONCE_WAS -> GameMap.ENDLESS_PARADOX;
                            case PLAINS_OF_DUNESTAR -> GameMap.ENDLESS_PARADOX;
                        };
                        queueEntryBuilder.setMap(map);
                        queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                    });
                }
        );
        // TODO: Use DB
        menu.setItem(
                2, 4,
                new ItemBuilder(Material.IRON_BLOCK)
                        .name(Component.text("Weekly limit - ", NamedTextColor.GRAY).append(Component.text("0/3", NamedTextColor.GREEN)))
                        .get(),
                (m, e) -> {}
        );
        // TODO: Use DB
        menu.setItem(
                6, 4,
                new ItemBuilder(Material.DRAGON_BREATH)
                        .name(Component.text("Booster Vials:", NamedTextColor.GRAY)
                                .append(Component.empty())
                                .append(Component.text("No active vials!", NamedTextColor.RED))
                        )
                        .get(),
                (m, e) -> {}
        );
        menu.openForPlayer(player);
    }
}

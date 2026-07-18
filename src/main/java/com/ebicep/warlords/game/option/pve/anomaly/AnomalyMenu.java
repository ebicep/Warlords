package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AnomalyMenu {

    private static final int[] REWARD_POOL_POSITIONS = {2, 4, 6};

    public static void openAnomalyMenu(Player player) {
        Menu menu = new Menu("Anomaly", 9 * 6);
        Anomalies currentAnomaly = AnomalyRotation.getCurrentAnomaly();
        NewItemsSetBonus featuredSet = AnomalyRotation.getGuaranteedLegendarySet();
        String timeTill = DateUtil.getTimeTill(AnomalyRotation.getNextRotation(), false, true, true, true);

        menu.setItem(
                4,
                0,
                new ItemBuilder(Material.CLOCK)
                        .name(Component.text("Rotation changes in ", NamedTextColor.GRAY)
                                .append(Component.text(timeTill, NamedTextColor.YELLOW)))
                        .lore(
                                Component.text("The active map, reward pools and", NamedTextColor.GRAY),
                                Component.text("featured Legendary set rotate hourly.", NamedTextColor.GRAY)
                        )
                        .get(),
                Menu.ACTION_DO_NOTHING
        );

        menu.setItem(
                4,
                2,
                new ItemBuilder(Material.AMETHYST_BLOCK)
                        .name(Component.text(currentAnomaly.getName(), NamedTextColor.GOLD, TextDecoration.BOLD))
                        .lore(currentAnomaly.getDescription())
                        .addLore(
                                Component.empty(),
                                Component.text("Map: ", NamedTextColor.GRAY)
                                        .append(Component.text(currentAnomaly.getMap().getMapName(), NamedTextColor.AQUA)),
                                Component.empty(),
                                Component.text("Click to begin", NamedTextColor.GREEN)
                        )
                        .get(),
                (m, e) -> GameStartCommand.startGamePvE(player, GameMode.ANOMALY, queueEntryBuilder -> {
                    queueEntryBuilder.setMap(currentAnomaly.getMap());
                    queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
                })
        );

        for (int i = 0; i < currentAnomaly.getRewardPools().size(); i++) {
            AnomalyRewardPool rewardPool = currentAnomaly.getRewardPools().get(i);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(currentAnomaly.getCacheObjective(i), NamedTextColor.GRAY));
            lore.add(Component.text("to earn this cache in your Reward Inventory.", NamedTextColor.GRAY));
            lore.add(Component.empty());
            lore.addAll(rewardPool.getLore());
            menu.setItem(
                    REWARD_POOL_POSITIONS[i],
                    4,
                    new ItemBuilder(Material.CHEST)
                            .name(Component.text(rewardPool.getName(), NamedTextColor.AQUA))
                            .lore(lore)
                            .get(),
                    Menu.ACTION_DO_NOTHING
            );
        }

        menu.setItem(
                4,
                5,
                new ItemBuilder(Material.GOLDEN_CHESTPLATE)
                        .name(Component.text("Featured Legendary Set", NamedTextColor.GOLD))
                        .lore(
                                Component.text(featuredSet.getName(), featuredSet.getTier().getTextColor()),
                                Component.empty(),
                                Component.text("If a cache's NewItem roll lands on", NamedTextColor.GRAY),
                                Component.text("the 2.5% Legendary outcome, its item", NamedTextColor.GRAY),
                                Component.text("will come from this featured set.", NamedTextColor.GRAY)
                        )
                        .glow()
                        .get(),
                Menu.ACTION_DO_NOTHING
        );

        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }
}
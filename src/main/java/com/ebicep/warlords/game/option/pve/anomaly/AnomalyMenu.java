package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class AnomalyMenu {

    private static final int[] REWARD_POOL_POSITIONS = {2, 4, 6};

    public static void openAnomalyMenu(Player player) {
        AnomalyCacheStreakLeaderboard.load(longestStreaks -> {
            if (player.isOnline()) {
                openAnomalyMenu(player, longestStreaks);
            }
        });
    }

    private static void openAnomalyMenu(Player player, List<DatabasePlayer> longestStreaks) {
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
                                Component.text("The active map, reward pools, second", NamedTextColor.GRAY),
                                Component.text("cache currency, and featured Legendary", NamedTextColor.GRAY),
                                Component.text("set rotate hourly.", NamedTextColor.GRAY)
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
                                Component.text("Max players: ", NamedTextColor.GRAY)
                                        .append(Component.text("2-6", NamedTextColor.AQUA)),
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

        List<Component> featuredSetLore = new ArrayList<>();
        featuredSetLore.add(Component.empty());
        featuredSetLore.add(Component.text(featuredSet.getName(), featuredSet.getTier().getTextColor()));
        featuredSetLore.add(Component.empty());
        featuredSetLore.addAll(featuredSet.getDescriptionLore());
        featuredSetLore.add(Component.empty());
        featuredSetLore.add(Component.text("If a cache's item roll lands on", NamedTextColor.GRAY));
        featuredSetLore.add(Component.text("the 2.5% Legendary outcome, its item", NamedTextColor.GRAY));
        featuredSetLore.add(Component.text("will come from this featured set.", NamedTextColor.GRAY));

        menu.setItem(
                4,
                5,
                new ItemBuilder(Material.GOLDEN_CHESTPLATE)
                        .name(Component.text("Featured Legendary Set", NamedTextColor.GOLD))
                        .lore(featuredSetLore)
                        .glow()
                        .get(),
                Menu.ACTION_DO_NOTHING
        );

        menu.setItem(0, 5, getStreakLeaderboardItem(player.getUniqueId(), longestStreaks), Menu.ACTION_DO_NOTHING);

        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    private static ItemStack getStreakLeaderboardItem(UUID viewer, List<DatabasePlayer> longestStreaks) {
        List<Pair<DatabasePlayer, Long>> entries = new ArrayList<>();
        for (DatabasePlayer databasePlayer : longestStreaks) {
            entries.add(new Pair<>(databasePlayer, getStreak(databasePlayer)));
        }
        entries.sort(Comparator.comparingLong((Pair<DatabasePlayer, Long> entry) -> entry.getB()).reversed());

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Caches claimed in a row without", NamedTextColor.GRAY));
        lore.add(Component.text("receiving a ", NamedTextColor.GRAY)
                          .append(Component.text("Legendary", NamedTextColor.GOLD))
                          .append(Component.text(" or ", NamedTextColor.GRAY))
                          .append(Component.text("Sovereign", NamedTextColor.LIGHT_PURPLE))
                          .append(Component.text(" item.", NamedTextColor.GRAY)));
        lore.add(Component.empty());
        if (entries.isEmpty()) {
            lore.add(Component.text("No one has an active streak yet.", NamedTextColor.GRAY));
        }
        for (int i = 0; i < entries.size(); i++) {
            Pair<DatabasePlayer, Long> entry = entries.get(i);
            boolean isViewer = entry.getA().getUuid().equals(viewer);
            lore.add(Component.text("#" + (i + 1) + " ", NamedTextColor.YELLOW)
                              .append(Component.text(entry.getA().getName(), isViewer ? NamedTextColor.GREEN : NamedTextColor.AQUA))
                              .append(Component.text(" - ", NamedTextColor.GRAY))
                              .append(Component.text(NumberFormat.addCommas(entry.getB()), NamedTextColor.RED)));
        }
        lore.add(Component.empty());
        long personalStreak = DatabaseManager.inCache(viewer, PlayersCollections.LIFETIME)
                              ? DatabaseManager.getPlayer(viewer).getHonorifics().getCleanAnomalyCacheStreak()
                              : 0;
        lore.add(Component.text("Your streak: ", NamedTextColor.GRAY)
                          .append(Component.text(NumberFormat.addCommas(personalStreak), NamedTextColor.RED)));
        lore.add(Component.empty());
        lore.add(Component.text("Resets to 0 when a cache gives you", NamedTextColor.DARK_GRAY));
        lore.add(Component.text("a Legendary or Sovereign item.", NamedTextColor.DARK_GRAY));
        return new ItemBuilder(Material.GOLD_BLOCK)
                .name(Component.text("Unlucky Cache Streaks", NamedTextColor.GOLD))
                .lore(lore)
                .get();
    }

    private static long getStreak(DatabasePlayer databasePlayer) {
        UUID uuid = databasePlayer.getUuid();
        if (DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return DatabaseManager.getPlayer(uuid).getHonorifics().getCleanAnomalyCacheStreak();
        }
        return databasePlayer.getHonorifics().getCleanAnomalyCacheStreak();
    }
}

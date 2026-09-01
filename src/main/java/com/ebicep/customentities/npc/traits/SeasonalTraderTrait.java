package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.HasNPCLabelHologram;
import com.ebicep.customentities.npc.NPCLabelHologram;
import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.SpendableBuyShopDistinct;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SeasonalTraderTrait extends WarlordsTrait implements HasNPCLabelHologram {

    private static final List<SpendableBuyShopDistinct> SHOP = List.of(
            new SpendableBuyShopDistinct(1, MobDrop.ZENITH_STAR, 5, 4_000_000, Currencies.COIN),
            new SpendableBuyShopDistinct(2000, Currencies.SYNTHETIC_SHARD, 2, 20, Currencies.ASCENDANT_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.LEGENDARY_STAR_PIECE, 3, 250, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.LIMIT_BREAKER, 3, 250, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_BANE_OF_IMPURITIES, 1, 500, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_GARDEN_OF_HESPERIDES, 1, 500, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_SPIDERS_BURROW, 1, 500, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_JUGGERNAUT, 1, 500, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 1, 500, Currencies.ILLUSION_SHARD)
    );

    public static void openSeasonalVendor(Player player, DatabasePlayer databasePlayer, DatabasePlayer databasePlayerMonthly) {
        Menu menu = new Menu("Seasonal Vendor", 9 * 4);

        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        DatabasePlayerPvE monthlyStats = databasePlayerMonthly.getPveStats();
        Map<String, Long> rewardsPurchased = monthlyStats.getSeasonalVendorRewardsPurchased();

        menu.setItem(3, 0,
                new ItemBuilder(Material.AMETHYST_SHARD)
                        .name(Currencies.ILLUSION_SHARD.getCostColoredName(pveStats.getCurrencyValue(Currencies.ILLUSION_SHARD)))
                        .get(),
                (m, e) -> {}
        );
        menu.setItem(5, 0,
                new ItemBuilder(Material.ECHO_SHARD)
                        .name(Currencies.ASCENDANT_SHARD.getCostColoredName(pveStats.getCurrencyValue(Currencies.ASCENDANT_SHARD)))
                        .get(),
                (m, e) -> {}
        );
        int x = 1;
        int y = 1;
        for (SpendableBuyShopDistinct reward : SHOP) {
            int rewardAmount = reward.amount();
            Spendable rewardSpendable = reward.spendable();
            int rewardPrice = reward.price();
            String mapName = reward.getMapName();
            Long purchasedAmount = rewardsPurchased.getOrDefault(mapName, 0L);

            String stock;
            if (reward.stock() == -1) {
                stock = "Unlimited";
            } else {
                stock = "" + (reward.stock() - purchasedAmount);
            }
            menu.setItem(x, y,
                    new ItemBuilder(rewardSpendable.getItem())
                            .name(rewardSpendable.getCostColoredName(rewardAmount))
                            .lore(
                                    Component.text("Cost: ", NamedTextColor.GRAY).append(reward.currency().getCostColoredName(rewardPrice)),
                                    Component.text("Stock: ", NamedTextColor.GRAY).append(Component.text(stock, NamedTextColor.YELLOW))
                            )
                            .get(),
                    (m, e) -> {
                        if (pveStats.getCurrencyValue(reward.currency()) < rewardPrice) {
                            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                        .append(reward.currency().getCostColoredName(rewardPrice))
                                                        .append(Component.text(" to purchase this item!"))
                            );
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                            return;
                        }
                        if (reward.stock() != -1 && purchasedAmount >= reward.stock()) {
                            player.sendMessage(Component.text("This item is out of stock!", NamedTextColor.RED));
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                            return;
                        }
                        pveStats.subtractCurrency(reward.currency(), rewardPrice);
                        rewardSpendable.addToPlayer(databasePlayer, rewardAmount);

                        pveStats.getSeasonalVendorRewardsPurchased().merge(mapName, 1L, Long::sum);
                        rewardsPurchased.merge(mapName, 1L, Long::sum);

                        player.sendMessage(Component.text("Purchased ", NamedTextColor.GREEN)
                                                    .append(rewardSpendable.getCostColoredName(rewardAmount))
                                                    .append(Component.text(" for "))
                                                    .append(reward.currency().getCostColoredName(rewardPrice))
                                                    .append(Component.text("!")));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2f);
                        openSeasonalVendor(player, databasePlayer, databasePlayerMonthly);

                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer, PlayersCollections.MONTHLY);
                    }
            );
            x++;
            if (x == 8) {
                x = 1;
                y++;
            }
        }
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    private final NPCLabelHologram labelHologram = new NPCLabelHologram("lobby-seasonal-trader");
    private int ticksElapsed = 0;

    public SeasonalTraderTrait() {
        super("SeasonalTraderTrait");
    }

    @Override
    public NPCLabelHologram getLabelHologram() {
        return labelHologram;
    }

    @Override
    public void onSpawn() {
        updateHologram(null);
    }

    @Override
    public void run() {
        if (ticksElapsed++ % 300 != 0) {
            return;
        }
        String timeTill = DateUtil.getTimeTill(DateUtil.getNextMonthlyResetDate(),
                true,
                true,
                true,
                false
        );
        if (!timeTill.equals("0 seconds")) {
            updateHologram(timeTill);
        }
    }

    private void updateHologram(String timeTill) {
        ComponentBuilder componentBuilder;
        if (timeTill != null) {
            componentBuilder = ComponentBuilder.create("Trader leaving in " + timeTill, NamedTextColor.RED, TextDecoration.BOLD)
                    .newLine("Spring Cleaner", NamedTextColor.GOLD);
        } else {
            componentBuilder = ComponentBuilder.create("Spring Cleaner", NamedTextColor.GOLD);
        }
        labelHologram.update(npc, componentBuilder.build());
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        UUID uuid = player.getUniqueId();
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
        DatabasePlayer databasePlayerMonthly = DatabaseManager.getPlayer(uuid, PlayersCollections.MONTHLY);
        openSeasonalVendor(player, databasePlayer, databasePlayerMonthly);
    }

}

package com.ebicep.customentities.npc.traits;

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
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SeasonalTraderTrait extends WarlordsTrait {

    private static final List<SpendableBuyShopDistinct> SHOP = List.of(
            new SpendableBuyShopDistinct(1, MobDrop.ZENITH_STAR, 5, 10, Currencies.ASCENDANT_SHARD),
            new SpendableBuyShopDistinct(4000, Currencies.SYNTHETIC_SHARD, 2, 20, Currencies.ASCENDANT_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.ASCENDANT_STAR_PIECE, 1, 25, Currencies.ASCENDANT_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.LEGENDARY_STAR_PIECE, 5, 250, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.LIMIT_BREAKER, 3, 250, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_BANE_OF_IMPURITIES, 1, 800, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_GARDEN_OF_HESPERIDES, 1, 800, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_SPIDERS_BURROW, 1, 800, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_JUGGERNAUT, 1, 800, Currencies.ILLUSION_SHARD),
            new SpendableBuyShopDistinct(1, Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 1, 800, Currencies.ILLUSION_SHARD)
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

    int ticksElapsed = 0;

    public SeasonalTraderTrait() {
        super("SeasonalTraderTrait");
    }

    @Override
    public void onAttach() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.GOLD + "Allegedly a Snowman");
    }

    @Override
    public void run() {
        if (ticksElapsed++ % 300 != 0) {
            return;
        }
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        String timeTill = DateUtil.getTimeTill(DateUtil.getNextMonthFirstDay(),
                true,
                true,
                true,
                false
        );
        if (!timeTill.equals("0 seconds")) {
            hologramTrait.setLine(1, ChatColor.RED.toString() + ChatColor.BOLD + "Trader leaving in " + timeTill);
        }
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

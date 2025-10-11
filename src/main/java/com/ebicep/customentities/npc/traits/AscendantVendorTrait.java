package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.SpendableBuyShop;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.versioned.WardenTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AscendantVendorTrait extends WarlordsTrait {

    private static final List<SpendableBuyShop> SHOP = List.of(
            new SpendableBuyShop(1, Currencies.ASCENDANT_SCROLL, 1, 25),
            new SpendableBuyShop(1, Currencies.ETHEREUM_CRYSTAL, 3, 5),
            new SpendableBuyShop(2000, Currencies.SYNTHETIC_SHARD, 1, 10),
            new SpendableBuyShop(1, Currencies.CRYPTIC_CONQUEST_KEY, 1, 10),
            new SpendableBuyShop(1, Currencies.SOVEREIGN_TOWER_KEY, 1, 10),
            new SpendableBuyShop(1, Currencies.ITEM_LOCK_SCROLL, 1, 20 )
    );

    public AscendantVendorTrait() {
        super("AscendantVendorTrait");
    }

    public static void openAscendantVendor(Player player, DatabasePlayer databasePlayer, DatabasePlayer databasePlayerWeekly) {
        Menu menu = new Menu("Ascendo", 9 * 4);

        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        DatabasePlayerPvE weeklyPveStats = databasePlayerWeekly.getPveStats();
        Map<String, Long> weeklyRewardsPurchased = weeklyPveStats.getIllusionVendorRewardsPurchased();

        menu.setItem(4, 0,
                new ItemBuilder(Material.ECHO_SHARD)
                        .name(Currencies.ASCENDANT_SHARD.getCostColoredName(pveStats.getCurrencyValue(Currencies.ASCENDANT_SHARD)))
                        .get(),
                (m, e) -> {

                }
        );
        for (int i = 0; i < SHOP.size(); i++) {
            SpendableBuyShop reward = SHOP.get(i);
            int rewardAmount = reward.amount();
            Spendable rewardSpendable = reward.spendable();
            int rewardPrice = reward.price();
            String mapName = reward.getMapName();
            Long purchasedAmount = weeklyRewardsPurchased.getOrDefault(mapName, 0L);

            String stock;
            if (reward.stock() == -1) {
                stock = "Unlimited";
            } else {
                stock = "" + (reward.stock() - purchasedAmount);
            }
            menu.setItem(i + 1, 1,
                    new ItemBuilder(rewardSpendable.getItem())
                            .name(rewardSpendable.getCostColoredName(rewardAmount))
                            .lore(
                                    Component.text("Cost: ", NamedTextColor.GRAY).append(Currencies.ASCENDANT_SHARD.getCostColoredName(rewardPrice)),
                                    Component.text("Stock: ", NamedTextColor.GRAY).append(Component.text(stock, NamedTextColor.YELLOW))
                            )
                            .get(),
                    (m, e) -> {
                        if (pveStats.getCurrencyValue(Currencies.ASCENDANT_SHARD) < rewardPrice) {
                            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                    .append(Currencies.ASCENDANT_SHARD.getCostColoredName(rewardPrice))
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
                        pveStats.subtractCurrency(Currencies.ASCENDANT_SHARD, rewardPrice);
                        rewardSpendable.addToPlayer(databasePlayer, rewardAmount);

                        player.sendMessage(Component.text("Purchased ", NamedTextColor.GREEN)
                                .append(rewardSpendable.getCostColoredName(rewardAmount))
                                .append(Component.text(" for "))
                                .append(Currencies.ASCENDANT_SHARD.getCostColoredName(rewardPrice))
                                .append(Component.text("!")));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2f);
                        openAscendantVendor(player, databasePlayer, databasePlayerWeekly);

                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayerWeekly, PlayersCollections.WEEKLY);
                    }
            );
        }
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    @Override
    public void onAttach() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.RED + "Ascendo");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        UUID uuid = player.getUniqueId();
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            DatabaseManager.getPlayer(uuid, PlayersCollections.WEEKLY, databasePlayerWeekly -> {
                openAscendantVendor(player, databasePlayer, databasePlayerWeekly);
            });
        });
    }
}

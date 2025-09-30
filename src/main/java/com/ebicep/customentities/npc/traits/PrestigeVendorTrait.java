package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.illusionvendor.pojos.IllusionVendorWeeklyShop;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.SpendableBuyShop;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PrestigeVendorTrait extends WarlordsTrait {

    private static final List<SpendableBuyShop> SHOP = List.of(
            new SpendableBuyShop(1, Currencies.CRYPTIC_CONQUEST_KEY, 1, 5),
            new SpendableBuyShop(1, Currencies.SOVEREIGN_TOWER_KEY, 1, 5),
            new SpendableBuyShop(1, Currencies.ETHERUM_CRYSTAL, 1, 5),
            new SpendableBuyShop(1, Currencies.ASCENDANT_SHARD, 1, 5)
    );

    public static void openPrestigeVendor(Player player, DatabasePlayer databasePlayer, DatabasePlayer databasePlayerWeekly) {
        Menu menu = new Menu("The Artificer", 9 * 4);

        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        DatabasePlayerPvE weeklyPveStats = databasePlayerWeekly.getPveStats();
        Map<String, Long> weeklyRewardsPurchased = weeklyPveStats.getIllusionVendorRewardsPurchased();

        menu.setItem(4, 0,
                new ItemBuilder(Material.HEART_OF_THE_SEA)
                        .name(Currencies.PRESTIGE_ORB.getCostColoredName(pveStats.getCurrencyValue(Currencies.PRESTIGE_ORB)))
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
                                    Component.text("Cost: ", NamedTextColor.GRAY).append(Currencies.PRESTIGE_ORB.getCostColoredName(rewardPrice)),
                                    Component.text("Stock: ", NamedTextColor.GRAY).append(Component.text(stock, NamedTextColor.YELLOW))
                            )
                            .get(),
                    (m, e) -> {
                        if (pveStats.getCurrencyValue(Currencies.PRESTIGE_ORB) < rewardPrice) {
                            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                    .append(Currencies.ILLUSION_SHARD.getCostColoredName(rewardPrice))
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
                        pveStats.subtractCurrency(Currencies.PRESTIGE_ORB, rewardPrice);
                        rewardSpendable.addToPlayer(databasePlayer, rewardAmount);

                        player.sendMessage(Component.text("Purchased ", NamedTextColor.GREEN)
                                .append(rewardSpendable.getCostColoredName(rewardAmount))
                                .append(Component.text(" for "))
                                .append(Currencies.PRESTIGE_ORB.getCostColoredName(rewardPrice))
                                .append(Component.text("!")));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2f);
                        openPrestigeVendor(player, databasePlayer, databasePlayerWeekly);

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
        hologramTrait.setLine(0, ChatColor.RED + "The Artificer");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        UUID uuid = player.getUniqueId();
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            DatabaseManager.getPlayer(uuid, PlayersCollections.WEEKLY, databasePlayerWeekly -> {
                openPrestigeVendor(player, databasePlayer, databasePlayerWeekly);
            });
        });
    }

    public PrestigeVendorTrait() {
        super("PrestigeVendorTrait");
    }
}

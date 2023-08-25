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
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
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

public class IllusionVendorTrait extends WarlordsTrait {

    private static final List<SpendableBuyShop> SHOP = List.of(
            new SpendableBuyShop(1, MobDrops.ZENITH_STAR, 3, 30),
            new SpendableBuyShop(200, Currencies.LEGEND_FRAGMENTS, 1, 20),
            new SpendableBuyShop(1, Currencies.CELESTIAL_BRONZE, 1, 500),
            new SpendableBuyShop(50, Currencies.FAIRY_ESSENCE, 1, 10),
            new SpendableBuyShop(1, Currencies.RARE_STAR_PIECE, 1, 10)
    );

    public static void openIllusionVendor(Player player, DatabasePlayer databasePlayer, DatabasePlayer databasePlayerWeekly) {
        Menu menu = new Menu("Illusion Vendor", 9 * 4);

        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        DatabasePlayerPvE weeklyPveStats = databasePlayerWeekly.getPveStats();
        Map<String, Long> weeklyRewardsPurchased = weeklyPveStats.getIllusionVendorRewardsPurchased();

        menu.setItem(4, 0,
                new ItemBuilder(Material.CHEST)
                        .name(Currencies.ILLUSION_SHARD.getCostColoredName(pveStats.getCurrencyValue(Currencies.ILLUSION_SHARD)))
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
            if (rewardSpendable == MobDrops.ZENITH_STAR) {
                rewardPrice += purchasedAmount * 10;
            }

            String stock;
            if (reward.stock() == -1) {
                stock = "Unlimited";
            } else {
                stock = "" + (reward.stock() - purchasedAmount);
            }
            int finalRewardPrice = rewardPrice;
            menu.setItem(i + 1, 1,
                    new ItemBuilder(rewardSpendable.getItem())
                            .name(rewardSpendable.getCostColoredName(rewardAmount))
                            .lore(
                                    Component.text("Cost: ", NamedTextColor.GRAY).append(Currencies.ILLUSION_SHARD.getCostColoredName(rewardPrice)),
                                    Component.text("Stock: ", NamedTextColor.GRAY).append(Component.text(stock, NamedTextColor.YELLOW))
                            )
                            .get(),
                    (m, e) -> {
                        if (pveStats.getCurrencyValue(Currencies.ILLUSION_SHARD) < finalRewardPrice) {
                            player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                        .append(Currencies.ILLUSION_SHARD.getCostColoredName(finalRewardPrice))
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
                        pveStats.subtractCurrency(Currencies.ILLUSION_SHARD, finalRewardPrice);
                        rewardSpendable.addToPlayer(databasePlayer, rewardAmount);

                        pveStats.getIllusionVendorRewardsPurchased().merge(mapName, 1L, Long::sum);
                        weeklyRewardsPurchased.merge(mapName, 1L, Long::sum);

                        player.sendMessage(Component.text("Purchased ", NamedTextColor.GREEN)
                                                    .append(rewardSpendable.getCostColoredName(rewardAmount))
                                                    .append(Component.text(" for "))
                                                    .append(Currencies.ILLUSION_SHARD.getCostColoredName(finalRewardPrice))
                                                    .append(Component.text("!")));
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2f);
                        openIllusionVendor(player, databasePlayer, databasePlayerWeekly);

                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayerWeekly, PlayersCollections.WEEKLY);
                    }
            );
        }
        IllusionVendorWeeklyShop weeklyShop = IllusionVendorWeeklyShop.currentIllusionVendorWeeklyShop;
        if (weeklyShop != null) {
            Map<String, IllusionVendorWeeklyShop.PurchasableItem> itemCosts = IllusionVendorWeeklyShop.ITEM_COSTS;
            AtomicInteger x = new AtomicInteger(1);
            weeklyShop.getItems()
                      .entrySet()
                      .stream()
                      .sorted(Comparator.comparing(o -> o.getValue().getTier())).forEachOrdered(entry -> {
                          String mapName = entry.getKey();
                          AbstractItem item = entry.getValue();
                          Component itemName = item.getItemName();
                          IllusionVendorWeeklyShop.PurchasableItem purchasableItem = itemCosts.get(mapName);
                          AbstractItem clone = item.clone();
                          if (purchasableItem == null || clone == null) {
                              ChatUtils.MessageType.ILLUSION_VENDOR.sendErrorMessage("Invalid item in weekly shop, report this!");
                              return;
                          }
                          long cost = purchasableItem.getCost();
                          Long purchasedAmount = weeklyRewardsPurchased.getOrDefault(mapName, 0L);
                          menu.setItem(x.get(), 2,
                                  new ItemBuilder(item.generateItemStack())
                                          .name(itemName)
                                          .addLore(
                                                  Component.empty(),
                                                  Component.text("Cost: ", NamedTextColor.GRAY).append(Currencies.ILLUSION_SHARD.getCostColoredName(cost)),
                                                  Component.text("Stock: ", NamedTextColor.GRAY).append(Component.text(1 - purchasedAmount, NamedTextColor.YELLOW))
                                          )
                                          .get(),
                                  (m, e) -> {
                                      if (pveStats.getCurrencyValue(Currencies.ILLUSION_SHARD) < cost) {
                                          player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                                      .append(Currencies.ILLUSION_SHARD.getCostColoredName(cost))
                                                                      .append(Component.text(" to purchase this item!"))
                                          );
                                          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                          return;
                                      }
                                      if (purchasedAmount >= 1) {
                                          player.sendMessage(Component.text("This item is out of stock!", NamedTextColor.RED));
                                          player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                          return;
                                      }
                                      pveStats.subtractCurrency(Currencies.ILLUSION_SHARD, cost);
                                      pveStats.getItemsManager().addItem(clone);

                                      pveStats.getIllusionVendorRewardsPurchased().merge(mapName, 1L, Long::sum);
                                      weeklyRewardsPurchased.merge(mapName, 1L, Long::sum);

                                      player.sendMessage(Component.text("Purchased ", NamedTextColor.GREEN)
                                                                  .append(itemName)
                                                                  .append(Component.text(" for "))
                                                                  .append(Currencies.ILLUSION_SHARD.getCostColoredName(cost))
                                                                  .append(Component.text("!"))
                                      );
                                      player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2f);
                                      openIllusionVendor(player, databasePlayer, databasePlayerWeekly);

                                      DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                      DatabaseManager.queueUpdatePlayerAsync(databasePlayerWeekly, PlayersCollections.WEEKLY);
                                  }
                          );
                          x.getAndIncrement();
                      });
        }
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.addBorder(Menu.GRAY_EMPTY_PANE, true);
        menu.openForPlayer(player);
    }

    private int ticksElapsed = 0;

    public IllusionVendorTrait() {
        super("IllusionVendorTrait");
    }

    @Override
    public void run() {
        if (ticksElapsed++ % 10 == 0) {
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
            hologramTrait.setLine(1, ChatColor.GREEN + "Illusion Vendor");
            String timeTill = DateUtil.getTimeTill(DateUtil.getResetDateLatestMonday(),
                    true,
                    true,
                    true,
                    false
            );
            if (!timeTill.equals("0 seconds")) {
                hologramTrait.setLine(2, ChatColor.GOLD.toString() + ChatColor.BOLD + "Next Shipment in " + timeTill);
            }
        }
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        UUID uuid = player.getUniqueId();
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            DatabaseManager.getPlayer(uuid, PlayersCollections.WEEKLY, databasePlayerWeekly -> {
                openIllusionVendor(player, databasePlayer, databasePlayerWeekly);
            });
        });
    }

}

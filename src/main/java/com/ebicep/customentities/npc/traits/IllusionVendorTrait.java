package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.HasNPCLabelHologram;
import com.ebicep.customentities.npc.NPCLabelHologram;
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
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class IllusionVendorTrait extends WarlordsTrait implements HasNPCLabelHologram {

    private static final List<SpendableBuyShop> SHOP = List.of(
            new SpendableBuyShop(1, MobDrop.ZENITH_STAR, 3, 30),
            new SpendableBuyShop(200, Currencies.LEGEND_FRAGMENTS, 1, 20),
            new SpendableBuyShop(50, Currencies.FAIRY_ESSENCE, 1, 10),
            new SpendableBuyShop(1, Currencies.RARE_STAR_PIECE, 1, 10),
            new SpendableBuyShop(1, Currencies.EPIC_STAR_PIECE, 1, 50),
            new SpendableBuyShop(500, Currencies.SYNTHETIC_SHARD, 1, 30)
    );

    public static void openIllusionVendor(Player player, DatabasePlayer databasePlayer, DatabasePlayer databasePlayerWeekly) {
        Menu menu = new Menu("Illusion Vendor", 9 * 4);

        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        DatabasePlayerPvE weeklyPveStats = databasePlayerWeekly.getPveStats();
        Map<String, Long> weeklyRewardsPurchased = weeklyPveStats.getIllusionVendorRewardsPurchased();

        menu.setItem(4, 0,
                new ItemBuilder(Material.AMETHYST_SHARD)
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
            if (rewardSpendable == MobDrop.ZENITH_STAR) {
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
            weeklyShop.getNewItems()
                      .entrySet()
                      .stream()
                      .sorted(Comparator.comparing(entry -> entry.getValue().getTier()))
                      .forEachOrdered(entry -> {
                          String mapName = entry.getKey();
                          NewItem item = entry.getValue();
                          Component itemName = item.getName();
                          IllusionVendorWeeklyShop.PurchasableItem purchasableItem = itemCosts.get(mapName);
                          if (purchasableItem == null) {
                              ChatUtils.MessageType.ILLUSION_VENDOR.sendErrorMessage("Invalid new item in weekly shop, report this!");
                              return;
                          }
                          NewItem clone = new NewItem(item);
                          long cost = purchasableItem.getCost();
                          Long purchasedAmount = weeklyRewardsPurchased.getOrDefault(mapName, 0L);
                          menu.setItem(x.get(), 2,
                                  item.getItemBuilder()
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
                                      pveStats.getNewItemsManager().addItem(clone);

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

    private final NPCLabelHologram labelHologram = new NPCLabelHologram("lobby-illusion-vendor");
    private int ticksElapsed = 0;

    public IllusionVendorTrait() {
        super("IllusionVendorTrait");
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
        if (ticksElapsed++ % 100 != 0) {
            return;
        }
        String timeTill = DateUtil.getTimeTill(DateUtil.getNextWeeklyResetDate(),
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
            componentBuilder = ComponentBuilder.create("Next Shipment in " + timeTill, NamedTextColor.GOLD, TextDecoration.BOLD)
                    .newLine("Illusion Vendor", NamedTextColor.GREEN);
        } else {
            componentBuilder = ComponentBuilder.create("Illusion Vendor", NamedTextColor.GREEN);
        }
        labelHologram.update(npc, componentBuilder.build());
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        UUID uuid = player.getUniqueId();
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
        DatabasePlayer databasePlayerWeekly = DatabaseManager.getPlayer(uuid, PlayersCollections.WEEKLY);
        openIllusionVendor(player, databasePlayer, databasePlayerWeekly);
    }

}

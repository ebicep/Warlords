package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.game.option.towerdefense.events.TowerSellEvent;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class TowerDefenseMenu {

    public static void openBuildMenu(Player player, AbstractTower tower) {
        Pair<Integer, Integer> ownerHeadPos = new Pair<>(2, 2);
        Pair<Integer, Integer> sellPos = new Pair<>(6, 3);

        Menu menu = new Menu(tower.getName(), 9 * 6);

        if (tower instanceof Upgradeable upgradeable) {
            ownerHeadPos.setA(7);
            ownerHeadPos.setB(1);
            sellPos.setA(7);
            sellPos.setB(3);
            upgradeable.addToMenu(menu, player, tower);
        }

        menu.setItem(ownerHeadPos.getA(), ownerHeadPos.getB(),
                new ItemBuilder(Material.PLAYER_HEAD) //HeadUtils.getHead(tower.getOwner()) TODO
                                                      .get(),
                (m, e) -> {

                }
        );
        menu.setItem(sellPos.getA(), sellPos.getB(),
                new ItemBuilder(Material.RED_CONCRETE)
                        .name(Component.text("Sell", NamedTextColor.RED))
                        .get(),
                (m, e) -> {
                    Bukkit.getPluginManager().callEvent(new TowerSellEvent(tower));
                    player.closeInventory();
                }
        );
        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openMarket(Player player, WarlordsEntity warlordsEntity, TowerDefensePlayerInfo playerInfo) {
        Menu menu = new Menu("Market", 9 * 6);

        menu.setItem(4, 0,
                new ItemBuilder(Material.BOOK)
                        .name(Component.text("INFO HERE"))
                        .get(),
                (m, e) -> {

                }
        );

        int currentRate = playerInfo.getCurrentInsigniaRate();
        List<TowerDefenseUtils.RateInfo> rateCosts = TowerDefenseUtils.INSIGNIA_RATE_EXP_COST;
        for (int i = 0; i < rateCosts.size(); i++) {
            TowerDefenseUtils.RateInfo rateInfo = rateCosts.get(i);
            int rate = rateInfo.rate();
            int expCost = rateInfo.expCost();
            if (currentRate != rate) {
                continue;
            }
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("Current Rate: ", NamedTextColor.GRAY).append(Component.text(rate + " ❂/sec", NamedTextColor.GOLD)));
            Material material;
            BiConsumer<Menu, InventoryClickEvent> clickHandler;
            if (i == rateCosts.size() - 1) {
                material = rateInfo.material();
                lore.add(Component.empty());
                lore.add(Component.text("MAXED!", NamedTextColor.AQUA, TextDecoration.BOLD));
                clickHandler = (m, e) -> {

                };
            } else {
                TowerDefenseUtils.RateInfo nextRateInfo = rateCosts.get(i + 1);
                material = nextRateInfo.material();
                lore.add(Component.text("Next Rate: ").append(Component.text(nextRateInfo.rate() + " ❂/sec", NamedTextColor.GOLD)));
                lore.add(Component.empty());
                lore.add(Component.text("Cost: ").append(Component.text(expCost + " exp", NamedTextColor.DARK_AQUA)));
                clickHandler = (m, e) -> {
                    if (playerInfo.getCurrentExp() >= expCost) {
                        playerInfo.addCurrentExp(-expCost);
                        playerInfo.setCurrentInsigniaRate(nextRateInfo.rate());
                        openMarket(player, warlordsEntity, playerInfo);
                    }
                };
            }
            menu.setItem(4, 2,
                    new ItemBuilder(material)
                            .name(Component.text("Upgrade Insignia Rate", NamedTextColor.GREEN))
                            .lore(lore)
                            .get(),
                    clickHandler
            );
        }


        // TODO buying special effects

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}

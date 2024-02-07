package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.game.option.towerdefense.events.TowerSellEvent;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TowerMenu {

    public static void openMenu(Player player, AbstractTower tower) {
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

}

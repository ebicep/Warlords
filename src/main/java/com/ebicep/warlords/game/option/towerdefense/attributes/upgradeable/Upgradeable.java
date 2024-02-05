package com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable;

import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;

public interface Upgradeable {

    default void tick() {
        getUpgrades().forEach(TowerUpgrade::tick);
    }

    void openUpgradeMenu(Player player, AbstractTower tower);

    List<TowerUpgrade> getUpgrades();

    enum UpgradeResult {
        SUCCESS,
        INSUFFICIENT_FUNDS,
        MISSING_REQUIREMENTS,
        ;

        public void onResult(Player player) {
            player.closeInventory();
        }
    }

    /**
     * <p>OOOO</p>
     * <p>XXXX</p>
     * <p>OOOO</p>
     */
    interface Path1 extends Upgradeable {

        @Override
        default void openUpgradeMenu(Player player, AbstractTower tower) {
            Menu menu = new Menu(tower.getTowerRegistry().name(), 9 * 6);

            List<TowerUpgrade> upgrades = getUpgrades();
            for (int i = 0; i < upgrades.size(); i++) {
                TowerUpgrade upgrade = upgrades.get(i);
                menu.setItem(i + 1, 1,
                        new ItemBuilder(upgrade.isUnlocked() ? Material.GREEN_CONCRETE : Material.WHITE_CONCRETE)
                                .lore(
                                        Component.text("SOMETHING HERE"),
                                        Component.empty()
                                )
                                .addLore(upgrade.getDescription())
                                .addLore(Component.empty())
                                .addLore(ComponentBuilder.create("Cost: ")
                                                         .text(NumberFormat.addCommaAndRound(upgrade.getCost()),
                                                                 NamedTextColor.GOLD
                                                         )
                                                         .build())
                                .get(),
                        Upgradeable.onUpgrade(player, upgrades, upgrade)
                );
            }

            menu.openForPlayer(player);
        }
    }

    @Nonnull
    private static BiConsumer<Menu, InventoryClickEvent> onUpgrade(Player player, List<TowerUpgrade> upgrades, TowerUpgrade upgrade) {
        return (m, e) -> {
            int index = upgrades.indexOf(upgrade);
            if (upgrade.getCost() > 0) { // TODO
                UpgradeResult.INSUFFICIENT_FUNDS.onResult(player);
                return;
            }
            if (index != 0 && !upgrades.get(index - 1).isUnlocked()) {
                UpgradeResult.MISSING_REQUIREMENTS.onResult(player);
                return;
            }
            upgrade.upgrade();
        };
    }

    /**
     * <p>OOXX</p>
     * <p>XXOO</p>
     * <p>OOXX</p>
     */
    interface Path2 extends Upgradeable {

        @Override
        default void openUpgradeMenu(Player player, AbstractTower tower) {
            Menu menu = new Menu(tower.getTowerRegistry().name(), 9 * 6);

            List<TowerUpgrade> upgrades = getUpgrades();
            addTwoUpgradesToMenu(player, menu, upgrades, 0, 1, 2);
            addTwoUpgradesToMenu(player, menu, upgrades, 2, 3, 1);
            addTwoUpgradesToMenu(player, menu, upgrades, 4, 3, 3);

            menu.openForPlayer(player);
        }

        private void addTwoUpgradesToMenu(
                Player player,
                Menu menu,
                List<TowerUpgrade> upgrades,
                int startIndex,
                int menuX,
                int menuY
        ) {
            if (upgrades.size() < startIndex) {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage(new Exception("startIndex is over upgrades size"));
                return;
            }
            for (int i = startIndex; i < upgrades.size() && i < startIndex + 2; i++) {
                TowerUpgrade upgrade = upgrades.get(i);
                menu.setItem(i + menuX, menuY,
                        new ItemBuilder(upgrade.isUnlocked() ? Material.GREEN_CONCRETE : Material.WHITE_CONCRETE)
                                .lore(
                                        Component.text("SOMETHING HERE"),
                                        Component.empty()
                                )
                                .addLore(upgrade.getDescription())
                                .addLore(Component.empty())
                                .addLore(ComponentBuilder.create("Cost: ")
                                                         .text(NumberFormat.addCommaAndRound(upgrade.getCost()),
                                                                 NamedTextColor.GOLD
                                                         )
                                                         .build())
                                .get(),
                        Upgradeable.onUpgrade(player, upgrades, upgrade)
                );
            }
        }

    }


}

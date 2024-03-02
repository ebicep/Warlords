package com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable;

import com.ebicep.warlords.game.option.towerdefense.TowerDefenseMenu;
import com.ebicep.warlords.game.option.towerdefense.events.TowerUpgradeEvent;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;

public interface Upgradeable {

    @Nonnull
    private static <T extends AbstractTower & Upgradeable> BiConsumer<Menu, InventoryClickEvent> onUpgrade(
            Player player,
            T tower,
            List<TowerUpgrade> upgrades,
            TowerUpgrade upgrade
    ) {
        return (m, e) -> {
            int index = upgrades.indexOf(upgrade);
            if (upgrade.getCost() > 0) { // TODO
                UpgradeResult.INSUFFICIENT_FUNDS.onResult(player);
                return;
            }
            if (index != 0 && !tower.previousUnlocked(upgrades, index)) {
                UpgradeResult.MISSING_REQUIREMENTS.onResult(player);
                return;
            }
            BlockData[][][] upgradeBlockData = tower.getTowerRegistry().upgradeTowerData.get(index);
            if (upgradeBlockData != null) {
                AbstractTower.build(tower.getCornerLocation(), upgradeBlockData);
            }
            Utils.playGlobalSound(tower.getCenterLocation(), Sound.BLOCK_ANVIL_USE, 2, 1);
            // TODO particle effects?
            upgrade.upgrade();
            tower.updateAttributes();
            Bukkit.getPluginManager().callEvent(new TowerUpgradeEvent(tower));
            TowerDefenseMenu.openBuildMenu(player, tower);
        };
    }

    default void tick() {
        getUpgrades().forEach(TowerUpgrade::tick);
    }

    List<TowerUpgrade> getUpgrades();

    <T extends AbstractTower & Upgradeable> void addToMenu(Menu menu, Player player, T tower);

    boolean previousUnlocked(List<TowerUpgrade> upgrades, int index);

    enum UpgradeResult {
        SUCCESS,
        INSUFFICIENT_FUNDS,
        MISSING_REQUIREMENTS,
        ;

        public void onResult(Player player) {
            player.sendMessage("Upgrade result: " + this);
            player.closeInventory();
        }
    }

    /**
     * <p>OOOO</p>
     * <p>XXXX</p>
     * <p>OOOO</p>
     */
    interface Path1 extends Upgradeable {

        int MAX_UPGRADES = 4;

        @Override
        default <T extends AbstractTower & Upgradeable> void addToMenu(Menu menu, Player player, T tower) {
            List<TowerUpgrade> upgrades = getUpgrades();
            if (upgrades.size() > MAX_UPGRADES) {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage(new Exception(tower + " has too many upgrades"));
                upgrades = upgrades.subList(0, MAX_UPGRADES);
            }
            for (int i = 0; i < upgrades.size(); i++) {
                TowerUpgrade upgrade = upgrades.get(i);
                menu.setItem(i + 1, 2,
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
                        Upgradeable.onUpgrade(player, tower, upgrades, upgrade)
                );
            }
        }

        @Override
        default boolean previousUnlocked(List<TowerUpgrade> upgrades, int index) {
            if (index == 0) {
                return true;
            }
            return upgrades.get(index - 1).isUnlocked();
        }

    }

    /**
     * <p>OOX</p>
     * <p>XXO</p>
     * <p>OOX</p>
     */
    interface Path2 extends Upgradeable {

        int MAX_UPGRADES = 4;

        @Override
        default <T extends AbstractTower & Upgradeable> void addToMenu(Menu menu, Player player, T tower) {
            List<TowerUpgrade> upgrades = getUpgrades();
            if (upgrades.size() > MAX_UPGRADES) {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage(new Exception(tower + " has too many upgrades"));
                upgrades = upgrades.subList(0, MAX_UPGRADES);
            }
            addUpgradeToMenu(player, menu, tower, upgrades, 0, 1, 2);
            addUpgradeToMenu(player, menu, tower, upgrades, 1, 2, 2);
            addUpgradeToMenu(player, menu, tower, upgrades, 2, 3, 1);
            addUpgradeToMenu(player, menu, tower, upgrades, 3, 3, 3);
        }

        @Override
        default boolean previousUnlocked(List<TowerUpgrade> upgrades, int index) {
            if (index == 0) {
                return true;
            }
            if (index == 3) {
                return upgrades.get(1).isUnlocked();
            }
            return upgrades.get(index - 1).isUnlocked();
        }

        private <T extends AbstractTower & Upgradeable> void addUpgradeToMenu(
                Player player,
                Menu menu,
                T tower,
                List<TowerUpgrade> upgrades,
                int i,
                int menuX,
                int menuY
        ) {
            if (upgrades.size() < i) {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage(new Exception("startIndex is over upgrades size"));
                return;
            }
            TowerUpgrade upgrade = upgrades.get(i);
            menu.setItem(menuX, menuY,
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
                    Upgradeable.onUpgrade(player, tower, upgrades, upgrade)
            );

        }

    }


}

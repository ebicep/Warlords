package com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable;

import com.ebicep.warlords.game.option.towerdefense.TowerDefenseMenu;
import com.ebicep.warlords.game.option.towerdefense.events.TowerUpgradeEvent;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

public interface Upgradeable {

    private static <T extends AbstractTower & Upgradeable> void addUpgradeToMenu(
            Player player,
            @Nullable WarlordsEntity warlordsEntity,
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
        if (upgrades.size() <= i) {
            return;
        }
        TowerUpgrade upgrade = upgrades.get(i);
        float cost = upgrade.getCost();
        Material material = Material.RED_STAINED_GLASS_PANE;
        if (upgrade.isUnlocked()) {
            material = Material.LIME_STAINED_GLASS_PANE;
        } else if (tower.isPreviousUnlocked(upgrades, i) && warlordsEntity != null && warlordsEntity.getCurrency() > cost) {
            material = Material.ORANGE_STAINED_GLASS_PANE;
        }
        ItemBuilder itemBuilder = new ItemBuilder(material)
                .name(Component.text(upgrade.getName(), NamedTextColor.GOLD))
                .lore(Component.empty())
                .addLore(upgrade.getDescription())
                .addLore(Component.empty());
        if (upgrade.isUnlocked()) {
            itemBuilder.addLore(ComponentBuilder.create("UNLOCKED", NamedTextColor.GREEN, TextDecoration.BOLD).build());
        } else {
            itemBuilder.addLore(ComponentBuilder.create("Cost: ")
                                                .text("❂ " + NumberFormat.addCommaAndRound(cost) + " Insignia", NamedTextColor.GOLD)
                                                .build());
        }
        menu.setItem(menuX, menuY,
                itemBuilder.get(),
                Upgradeable.onUpgrade(player, warlordsEntity, tower, upgrades, upgrade)
        );
    }

    boolean isPreviousUnlocked(List<TowerUpgrade> upgrades, int index);

    @Nonnull
    private static <T extends AbstractTower & Upgradeable> BiConsumer<Menu, InventoryClickEvent> onUpgrade(
            Player player,
            WarlordsEntity warlordsEntity,
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
            if (index != 0 && !tower.isPreviousUnlocked(upgrades, index)) {
                UpgradeResult.MISSING_REQUIREMENTS.onResult(player);
                return;
            }
            BlockData[][][] upgradeBlockData = tower.getTowerRegistry().upgradeTowerData.get(index);
            if (upgradeBlockData != null) {
                AbstractTower.build(tower.getCornerLocation(), upgradeBlockData);
            }
            Utils.playGlobalSound(tower.getTopCenterLocation(), Sound.BLOCK_ANVIL_USE, 2, 1);
            // TODO particle effects?
            upgrade.upgrade();
            Bukkit.getPluginManager().callEvent(new TowerUpgradeEvent(tower));
            TowerDefenseMenu.openTowerMenu(player, warlordsEntity, tower);
        };
    }

    default void tick() {
        getUpgrades().forEach(TowerUpgrade::tick);
    }

    List<TowerUpgrade> getUpgrades();

    <T extends AbstractTower & Upgradeable> void addToMenu(Menu menu, Player player, WarlordsEntity warlordsEntity, T tower);

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

        int MAX_UPGRADES = 3;

        @Override
        default <T extends AbstractTower & Upgradeable> void addToMenu(Menu menu, Player player, WarlordsEntity warlordsEntity, T tower) {
            List<TowerUpgrade> upgrades = getUpgrades();
            if (upgrades.size() > MAX_UPGRADES) {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage(new Exception(tower + " has too many upgrades"));
                upgrades = upgrades.subList(0, MAX_UPGRADES);
            }
            for (int i = 0; i < upgrades.size(); i++) {
                Upgradeable.addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, i, 5 + i, 2);
            }
        }

        @Override
        default boolean isPreviousUnlocked(List<TowerUpgrade> upgrades, int index) {
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
        default <T extends AbstractTower & Upgradeable> void addToMenu(Menu menu, Player player, WarlordsEntity warlordsEntity, T tower) {
            List<TowerUpgrade> upgrades = getUpgrades();
            if (upgrades.size() > MAX_UPGRADES) {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage(new Exception(tower + " has too many upgrades"));
                upgrades = upgrades.subList(0, MAX_UPGRADES);
            }
            addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, 0, 5, 2);
            addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, 1, 6, 2);
            addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, 2, 7, 1);
            addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, 3, 7, 3);
        }

        @Override
        default boolean isPreviousUnlocked(List<TowerUpgrade> upgrades, int index) {
            if (index == 0) {
                return true;
            }
            if (index == 3) {
                return upgrades.get(1).isUnlocked();
            }
            return upgrades.get(index - 1).isUnlocked();
        }

    }


}

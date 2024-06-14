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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
        if (tower.isUpgradeLocked(upgrades, i)) {
            material = Material.GRAY_STAINED_GLASS_PANE;
        } else if (upgrade.isUnlocked()) {
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
                (m, e) -> {
                    UpgradeResult result = Upgradeable.onUpgrade(player, warlordsEntity, tower, upgrades, upgrade, false);
                    result.onResult(player);
                }
        );
    }

    boolean isUpgradeLocked(List<TowerUpgrade> upgrades, int index);

    boolean isPreviousUnlocked(List<TowerUpgrade> upgrades, int index);

    @Nonnull
    static <T extends AbstractTower & Upgradeable> UpgradeResult onUpgrade(
            Player player,
            WarlordsEntity warlordsEntity,
            T tower,
            List<TowerUpgrade> upgrades,
            TowerUpgrade upgrade,
            boolean sneakUpgraded
    ) {
        int index = upgrades.indexOf(upgrade);
        UpgradeResult result = canUpgrade(tower, upgrades, upgrade, index);
        if (result != UpgradeResult.SUCCESS) {
            return result;
        }
        BlockData[][][] upgradeBlockData = tower.getTowerRegistry().upgradeTowerData.get(index);
        if (upgradeBlockData != null) {
            AbstractTower.build(tower.getCornerLocation(), upgradeBlockData);
        }
        Utils.playGlobalSound(tower.getTopCenterLocation(), Sound.BLOCK_ANVIL_USE, 2, 1);
        // TODO particle effects?
        upgrade.upgrade();
        Bukkit.getPluginManager().callEvent(new TowerUpgradeEvent<>(tower, warlordsEntity, upgrade, sneakUpgraded));
        if (!sneakUpgraded) {
            TowerDefenseMenu.openTowerMenu(player, warlordsEntity, tower);
        }
        return UpgradeResult.SUCCESS;
    }

    static <T extends AbstractTower & Upgradeable> UpgradeResult canUpgrade(T tower, List<TowerUpgrade> upgrades, TowerUpgrade upgrade, int index) {
        if (upgrade.isUnlocked()) {
            return UpgradeResult.ALREADY_UNLOCKED;
        }
        if (tower.isUpgradeLocked(upgrades, index)) {
            return UpgradeResult.LOCKED;
        }
        if (upgrade.getCost() > 0) { // TODO
//            return UpgradeResult.INSUFFICIENT_FUNDS;
        }
        if (index != 0 && !tower.isPreviousUnlocked(upgrades, index)) {
            return UpgradeResult.MISSING_REQUIREMENTS;
        }
        return UpgradeResult.SUCCESS;
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
        ALREADY_UNLOCKED,
        LOCKED,

        ;

        public void onResult(Player player) {
            player.sendMessage(Component.text("Upgrade Result: " + this, NamedTextColor.GOLD));
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
        default boolean isUpgradeLocked(List<TowerUpgrade> upgrades, int index) {
            return false;
        }

        @Override
        default boolean isPreviousUnlocked(List<TowerUpgrade> upgrades, int index) {
            if (index == 0) {
                return true;
            }
            return upgrades.get(index - 1).isUnlocked();
        }

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

    }

    /**
     * <p>OOOX</p>
     * <p>XXXO</p>
     * <p>OOOX</p>
     */
    interface Path2 extends Upgradeable {

        int MAX_UPGRADES = 5;

        @Override
        default boolean isUpgradeLocked(List<TowerUpgrade> upgrades, int index) {
            if (index == 3) {
                return upgrades.get(4).isUnlocked();
            }
            if (index == 4) {
                return upgrades.get(3).isUnlocked();
            }
            return false;
        }

        @Override
        default boolean isPreviousUnlocked(List<TowerUpgrade> upgrades, int index) {
            if (index == 0) {
                return true;
            }
            if (index == MAX_UPGRADES - 1) {
                return upgrades.get(2).isUnlocked();
            }
            return upgrades.get(index - 1).isUnlocked();
        }

        @Override
        default <T extends AbstractTower & Upgradeable> void addToMenu(Menu menu, Player player, WarlordsEntity warlordsEntity, T tower) {
            List<TowerUpgrade> upgrades = getUpgrades();
            if (upgrades.size() > MAX_UPGRADES) {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage(new Exception(tower + " has too many upgrades"));
                upgrades = upgrades.subList(0, MAX_UPGRADES);
            }
            addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, 0, 4, 2);
            addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, 1, 5, 2);
            addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, 2, 6, 2);
            addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, 3, 7, 1);
            addUpgradeToMenu(player, warlordsEntity, menu, tower, upgrades, 4, 7, 3);
        }

    }


}

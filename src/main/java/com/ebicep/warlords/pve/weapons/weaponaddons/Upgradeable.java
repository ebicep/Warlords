package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public interface Upgradeable {

    void upgrade();

    default float getUpgradeMultiplier() {
        return 1.1f;
    }

    default float getUpgradeMultiplierNegative() {
        return 1;
    }

    default ItemStack getUpgradeItem() {
        List<String> upgradeLore = new ArrayList<>(getUpgradeLore());
        upgradeLore.add("");
        upgradeLore.add(ChatColor.LIGHT_PURPLE + "Upgrade Level [" + getUpgradeLevel() + "/" + getMaxUpgradeLevel() + "]" + ChatColor.GREEN + " > " + ChatColor.LIGHT_PURPLE + "[" + (getUpgradeLevel() + 1) + "/" + getMaxUpgradeLevel() + "]");
        upgradeLore.addAll(getUpgradeCostLore());
        return new ItemBuilder(Material.GREEN_CONCRETE)
                .name(ChatColor.GREEN + "Confirm")
                .loreLEGACY(upgradeLore)
                .get();
    }

    List<String> getUpgradeLore();

    int getUpgradeLevel();

    int getMaxUpgradeLevel();

    default List<String> getUpgradeCostLore() {
        LinkedHashMap<Currencies, Long> upgradeCost = getUpgradeCost(getUpgradeLevel() + 1);
        if (upgradeCost.isEmpty()) {
            return Collections.singletonList(ChatColor.LIGHT_PURPLE + "Max Level!");
        } else {
            return PvEUtils.getCostLore(upgradeCost, "Upgrade Cost", true);
        }
    }

    LinkedHashMap<Currencies, Long> getUpgradeCost(int tier);

}

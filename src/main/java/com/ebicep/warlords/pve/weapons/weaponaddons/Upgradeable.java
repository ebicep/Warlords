package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public interface Upgradeable {

    void upgrade();

    default float getUpgradeMultiplier() {
        return 1.1f;
    }

    default float getUpgradeMultiplierNegative() {
        return 0.9f;
    }

    default ItemStack getUpgradeItem() {
        List<String> upgradeLore = new ArrayList<>(getUpgradeLore());
        upgradeLore.add("");
        upgradeLore.add(ChatColor.LIGHT_PURPLE + "Upgrade Level [" + getUpgradeLevel() + "/" + getMaxUpgradeLevel() + "]" + ChatColor.GREEN + " > " + ChatColor.LIGHT_PURPLE + "[" + (getUpgradeLevel() + 1) + "/" + getMaxUpgradeLevel() + "]");
        upgradeLore.add("");
        upgradeLore.add(ChatColor.AQUA + "Upgrade Cost: ");
        getUpgradeCost(getUpgradeLevel() + 1).forEach((currency, amount) -> upgradeLore.add(ChatColor.GRAY + " - " + ChatColor.GREEN +
                NumberFormat.addCommas(amount) + " " + currency.getColoredName() + "s"));
        return new ItemBuilder(Material.STAINED_CLAY, 1, (short) 13)
                .name(ChatColor.GREEN + "Confirm")
                .lore(upgradeLore)
                .get();
    }

    List<String> getUpgradeLore();

    int getUpgradeLevel();

    int getMaxUpgradeLevel();

    LinkedHashMap<Currencies, Long> getUpgradeCost(int tier);

}

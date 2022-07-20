package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface Upgradeable {

    void upgrade();

    default float getUpgradeMultiplier() {
        return 1.1f;
    }

    default ItemStack getUpgradeItem() {
        List<String> upgradeLore = new ArrayList<>(getUpgradeLore());
        upgradeLore.add("");
        upgradeLore.add(ChatColor.LIGHT_PURPLE + "Upgrade Level [" + getUpgradeLevel() + "/" + getMaxUpgradeLevel() + "]" + ChatColor.GREEN + " > " + ChatColor.LIGHT_PURPLE + "[" + (getUpgradeLevel() + 1) + "/" + getMaxUpgradeLevel() + "]");
        return new ItemBuilder(Material.STAINED_CLAY, 1, (short) 13)
                .name(ChatColor.GREEN + "Confirm")
                .lore(upgradeLore)
                .get();
    }

    List<String> getUpgradeLore();

    int getMaxUpgradeLevel();

    int getUpgradeLevel();

}

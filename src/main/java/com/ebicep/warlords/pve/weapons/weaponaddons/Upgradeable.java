package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        List<Component> upgradeLore = new ArrayList<>(getUpgradeLore());
        upgradeLore.add(Component.empty());
        upgradeLore.add(Component.text("Upgrade Level [" + getUpgradeLevel() + "/" + getMaxUpgradeLevel() + "]", NamedTextColor.LIGHT_PURPLE)
                                 .append(AbstractWeapon.GREEN_ARROW)
                                 .append(Component.text("[" + (getUpgradeLevel() + 1) + "/" + getMaxUpgradeLevel() + "]"))
        );
        upgradeLore.addAll(getUpgradeCostLore());
        return new ItemBuilder(Material.GREEN_CONCRETE)
                .name(Component.text("Confirm", NamedTextColor.GREEN))
                .lore(upgradeLore)
                .get();
    }

    List<Component> getUpgradeLore();

    int getUpgradeLevel();

    int getMaxUpgradeLevel();

    default List<Component> getUpgradeCostLore() {
        LinkedHashMap<Currencies, Long> upgradeCost = getUpgradeCost(getUpgradeLevel() + 1);
        if (upgradeCost.isEmpty()) {
            return Collections.singletonList(Component.text("Max Level!", NamedTextColor.LIGHT_PURPLE));
        } else {
            return PvEUtils.getCostLore(upgradeCost, "Upgrade Cost", true);
        }
    }

    LinkedHashMap<Currencies, Long> getUpgradeCost(int tier);

}

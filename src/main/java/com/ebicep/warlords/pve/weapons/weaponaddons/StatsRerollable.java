package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.pve.Currencies;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public interface StatsRerollable {

    int getRerollCost();

    void reroll();

    default List<String> getRerollCostLore() {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.AQUA + "Cost: ");
        lore.add(ChatColor.GRAY + " - " + Currencies.COIN.getCostColoredName(getRerollCost()));
        return lore;
    }

}

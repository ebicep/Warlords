package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PvEUtils {

    public static <T extends Spendable> List<String> getCostLore(LinkedHashMap<T, Long> cost) {
        return new ArrayList<>() {{
            add("");
            add(ChatColor.AQUA + "Cost: ");
            cost.forEach((spendable, amount) -> add(ChatColor.GRAY + " - " + spendable.getCostColoredName(amount)));
        }};
    }

    public static <T extends Spendable> List<String> getCostLore(LinkedHashMap<T, Long> cost, String costName) {
        return new ArrayList<>() {{
            add("");
            add(ChatColor.AQUA + costName + ": ");
            cost.forEach((spendable, amount) -> add(ChatColor.GRAY + " - " + spendable.getCostColoredName(amount)));
        }};
    }

}

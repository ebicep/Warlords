package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PvEUtils {

    public static <T extends Spendable> List<String> getCostLore(LinkedHashMap<T, Long> cost, boolean emptyLine) {
        return new ArrayList<>() {{
            if (emptyLine) {
                add("");
            }
            add(ChatColor.AQUA + "Cost: ");
            cost.forEach((spendable, amount) -> add(ChatColor.GRAY + " - " + spendable.getCostColoredName(amount)));
        }};
    }

    public static <T extends Spendable> List<String> getCostLore(LinkedHashMap<T, Long> cost, String costName, boolean emptyLine) {
        return new ArrayList<>() {{
            if (emptyLine) {
                add("");
            }
            add(ChatColor.AQUA + costName + ": ");
            cost.forEach((spendable, amount) -> add(ChatColor.GRAY + " - " + spendable.getCostColoredName(amount)));
        }};
    }

}

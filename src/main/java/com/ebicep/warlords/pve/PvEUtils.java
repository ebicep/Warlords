package com.ebicep.warlords.pve;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PvEUtils {

    public static <T extends Spendable> List<Component> getCostLore(LinkedHashMap<T, Long> cost, boolean emptyLine) {
        return new ArrayList<>() {{
            if (emptyLine) {
                add(Component.empty());
            }
            add(Component.text("Cost: ", NamedTextColor.AQUA));
            cost.forEach((spendable, amount) -> add(Component.text(" - ", NamedTextColor.GRAY).append(spendable.getCostColoredName(amount))));
        }};
    }

    public static <T extends Spendable> List<Component> getCostLore(LinkedHashMap<T, Long> cost, String costName, boolean emptyLine) {
        return new ArrayList<>() {{
            if (emptyLine) {
                add(Component.empty());
            }
            add(Component.text(costName + ": ", NamedTextColor.AQUA));
            cost.forEach((spendable, amount) -> add(Component.text(" - ", NamedTextColor.GRAY).append(spendable.getCostColoredName(amount))));
        }};
    }

}

package com.ebicep.warlords.pve;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PvEUtils {

    public static <T extends Spendable> List<Component> getCostLore(Map<T, Long> cost, boolean emptyLine) {
        return new ArrayList<>() {{
            if (emptyLine) {
                add(Component.empty());
            }
            add(Component.text("Cost: ", NamedTextColor.AQUA));
            cost.forEach((spendable, amount) -> add(Component.text(" - ", NamedTextColor.GRAY).append(spendable.getCostColoredName(amount))));
        }};
    }

    public static <T extends Spendable> List<Component> getCostLore(Map<T, Long> cost, @Nullable String costName, boolean emptyLine) {
        return new ArrayList<>() {{
            if (emptyLine) {
                add(Component.empty());
            }
            if (costName != null) {
                add(Component.text(costName + ": ", NamedTextColor.AQUA));
            }
            cost.forEach((spendable, amount) -> add(Component.text(" - ", NamedTextColor.GRAY).append(spendable.getCostColoredName(amount))));
        }};
    }

}

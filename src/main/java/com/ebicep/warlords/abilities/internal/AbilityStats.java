package com.ebicep.warlords.abilities.internal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;

public interface AbilityStats<T extends AbstractAbility, R extends AbstractAbilityStats<T, R>> {

    default Component getFormattedData(TextColor color) {
        TextComponent.Builder abilityInfo = Component.text();
        List<AbstractAbilityStats.AbilityStatDisplay> statsDisplay = getAbilityStats().getStatsDisplay();
        for (int i = 0; i < statsDisplay.size(); i++) {
            AbstractAbilityStats.AbilityStatDisplay statDisplay = statsDisplay.get(i);
            abilityInfo.append(Component.text(statDisplay.name() + ": ", NamedTextColor.WHITE))
                       .append(Component.text(statDisplay.value(), NamedTextColor.GOLD));
            if (i != statsDisplay.size() - 1) {
                abilityInfo.append(Component.newline());
            }
        }
        return Component.text(getName(), color).hoverEvent(HoverEvent.showText(abilityInfo));
    }

    R getAbilityStats();

    String getName();

}

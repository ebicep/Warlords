package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

public class CodexCollector implements FieldEffect {
    @Override
    public String getName() {
        return "Codex Collector";
    }

    @Override
    public String getDescription() {
        return "Players gain a special bonus based on the amount of codexes equipped.";
    }

    @Override
    public List<Component> getSubDescription() {
        return new ArrayList<>() {{
            add(Component.empty());
            add(Component.text("2 Codexes: ", NamedTextColor.DARK_RED)
                         .append(Component.text("Defeating an opponent instantly restores 5% of max HP.")));
            add(Component.text("4 Codexes: ", NamedTextColor.DARK_RED)
                         .append(Component.text("+5% Crit Chance and +10 Crit Multiplier.")));
            add(Component.text("6 Codexes: ", NamedTextColor.DARK_RED)
                         .append(Component.text("Defeating an opponent with a rune ability has a 25% chance of ending its cooldown")));
        }};
    }
}

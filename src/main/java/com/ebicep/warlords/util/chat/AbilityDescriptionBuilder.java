package com.ebicep.warlords.util.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class AbilityDescriptionBuilder {

    private TextComponent description;

    public AbilityDescriptionBuilder() {
        description = Component.empty();
    }

    public AbilityDescriptionBuilder append(TextComponent component) {
        return this;
    }

    public TextComponent build() {
        return description;
    }
}

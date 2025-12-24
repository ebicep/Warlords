package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class NewItemsUtils {

    public static Component createStarComponent(TextColor textColor, int starCount) {
        if (starCount <= 0 || starCount > 6) {
            throw new IllegalArgumentException("starCount must be between 0 and 6");
        }
        ComponentBuilder builder = ComponentBuilder.create().text("[", NamedTextColor.GRAY);
        if (starCount == 6) {
            builder.text("❂".repeat(6), textColor);
        } else {
            builder.text("❂".repeat(starCount), textColor)
                   .text("❂".repeat(6 - starCount), NamedTextColor.DARK_GRAY);
        }
        builder.text("]", NamedTextColor.GRAY);
        return builder.build();
    }

}

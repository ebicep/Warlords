package com.ebicep.warlords.guilds.logs.types.oneplayer.tag;

import com.ebicep.warlords.guilds.logs.types.oneplayer.AbstractGuildLogOnePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public class GuildLogTagNameColor extends AbstractGuildLogOnePlayer {

    @Field("old_name")
    private String oldColor;
    @Field("new_name")
    private String newColor;

    public GuildLogTagNameColor(UUID sender, String oldColor, String newColor) {
        super(sender);
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    @Override
    public Component getAction() {
        NamedTextColor oldTextColor = NamedTextColor.NAMES.value(oldColor);
        NamedTextColor newTextColor = NamedTextColor.NAMES.value(newColor);
        String oldTextColorName;
        String newTextColorName;
        if (oldTextColor != null) {
            oldTextColorName = oldTextColor.toString().replace("_", " ");
        } else {
            oldTextColorName = "UNKNOWN";
        }
        if (newTextColor != null) {
            newTextColorName = newTextColor.toString().replace("_", " ");
        } else {
            newTextColorName = "UNKNOWN";
        }
        return Component.text("changed guild tag name color from ")
                        .append(Component.text(oldTextColorName, oldTextColor))
                        .append(Component.text(" to "))
                        .append(Component.text(newTextColorName, newTextColor));
    }
}

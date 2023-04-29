package com.ebicep.warlords.guilds.logs.types.oneplayer.tag;

import com.ebicep.warlords.guilds.logs.types.oneplayer.AbstractGuildLogOnePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public class GuildLogTagChangeName extends AbstractGuildLogOnePlayer {

    @Field("old_name")
    private String oldName;
    @Field("new_name")
    private String newName;

    public GuildLogTagChangeName(UUID sender, String oldName, String newName) {
        super(sender);
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public Component getAction() {
        return Component.text("changed guild tag name from ").append(Component.text(oldName, NamedTextColor.GREEN))
                        .append(Component.text(" to "))
                        .append(Component.text(newName));
    }
}

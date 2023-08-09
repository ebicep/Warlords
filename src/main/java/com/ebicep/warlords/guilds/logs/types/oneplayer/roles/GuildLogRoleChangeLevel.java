package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public class GuildLogRoleChangeLevel extends AbstractGuildLogRole {

    @Field("old_level")
    private int oldLevel;
    @Field("new_level")
    private int newLevel;

    public GuildLogRoleChangeLevel(UUID sender, String role, int oldLevel, int newLevel) {
        super(sender, role);
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    @Override
    public Component getAction() {
        return Component.text("changed level of");
    }

    @Override
    public Component append() {
        return Component.text("from ")
                        .append(Component.text(oldLevel, NamedTextColor.GREEN))
                        .append(Component.text(" to "))
                        .append(Component.text(newLevel, NamedTextColor.GREEN));
    }
}

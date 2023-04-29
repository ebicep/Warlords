package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public class GuildLogRoleRename extends AbstractGuildLogRole {

    @Field("new_name")
    private String newName;

    public GuildLogRoleRename(UUID sender, String role, String newName) {
        super(sender, role);
        this.newName = newName;
    }

    @Override
    public String getAction() {
        return "renamed";
    }

    @Override
    public Component append() {
        return Component.text("to ").append(Component.text(newName, NamedTextColor.GREEN));
    }
}

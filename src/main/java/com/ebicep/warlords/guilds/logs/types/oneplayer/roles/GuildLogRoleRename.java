package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import org.bukkit.ChatColor;
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
    public String append() {
        return "to " + ChatColor.GREEN + newName;
    }
}

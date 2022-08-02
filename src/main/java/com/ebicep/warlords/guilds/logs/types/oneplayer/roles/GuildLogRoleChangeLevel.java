package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import org.bukkit.ChatColor;
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
    public String getAction() {
        return "changed level of";
    }

    @Override
    public String append() {
        return "from " + ChatColor.GREEN + oldLevel + ChatColor.GRAY + " to " + ChatColor.GREEN + newLevel;
    }
}

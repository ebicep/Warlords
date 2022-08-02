package com.ebicep.warlords.guilds.logs.types.oneplayer.roles.permissions;

import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.logs.types.oneplayer.roles.AbstractGuildLogRole;
import org.bukkit.ChatColor;

import java.util.UUID;

public abstract class AbstractGuildLogRolePermission extends AbstractGuildLogRole {

    protected GuildPermissions permission;

    public AbstractGuildLogRolePermission(UUID sender, String role, GuildPermissions permission) {
        super(sender, role);
        this.permission = permission;
    }

    @Override
    public String getLog() {
        return ChatColor.GRAY + prepend() + " " + getSenderName() + " " + ChatColor.YELLOW + getAction() + " " + ChatColor.RED + permission.name() + ChatColor.GRAY + " permission to " + ChatColor.GREEN + role + " " + ChatColor.GRAY + append();
    }
}

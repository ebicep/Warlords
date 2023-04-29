package com.ebicep.warlords.guilds.logs.types.oneplayer.roles.permissions;

import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.logs.types.oneplayer.roles.AbstractGuildLogRole;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public abstract class AbstractGuildLogRolePermission extends AbstractGuildLogRole {

    protected GuildPermissions permission;

    public AbstractGuildLogRolePermission(UUID sender, String role, GuildPermissions permission) {
        super(sender, role);
        this.permission = permission;
    }

    @Override
    public Component getLog() {
        return Component.empty().color(NamedTextColor.GRAY)
                        .append(prepend())
                        .append(Component.space())
                        .append(getSenderName())
                        .append(Component.space())
                        .append(Component.text(getAction(), NamedTextColor.YELLOW))
                        .append(Component.space())
                        .append(Component.text(permission.name(), NamedTextColor.RED))
                        .append(Component.text(" permission to ", NamedTextColor.GRAY))
                        .append(Component.text(role, NamedTextColor.GREEN))
                        .append(Component.space())
                        .append(append());
    }
}

package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public class GuildLogRoleSetDefault extends AbstractGuildLogRole {

    public GuildLogRoleSetDefault(UUID sender, String role) {
        super(sender, role);
    }

    @Override
    public Component getAction() {
        return Component.text("set default role to");
    }
}

package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import java.util.UUID;

public class GuildLogRoleSetDefault extends AbstractGuildLogRole {

    public GuildLogRoleSetDefault(UUID sender, String role) {
        super(sender, role);
    }

    @Override
    public String getAction() {
        return "set default role to";
    }
}

package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import java.util.UUID;

public class GuildLogRoleCreate extends AbstractGuildLogRole {

    public GuildLogRoleCreate(UUID sender, String role) {
        super(sender, role);
    }

    @Override
    public String getAction() {
        return "created";
    }

}

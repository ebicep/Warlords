package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import java.util.UUID;

public class GuildLogRoleDelete extends AbstractGuildLogRole {

    public GuildLogRoleDelete(UUID sender, String role) {
        super(sender, role);
    }

    @Override
    public String getAction() {
        return "deleted";
    }
}

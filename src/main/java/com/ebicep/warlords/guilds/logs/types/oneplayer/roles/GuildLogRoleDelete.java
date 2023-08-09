package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public class GuildLogRoleDelete extends AbstractGuildLogRole {

    public GuildLogRoleDelete(UUID sender, String role) {
        super(sender, role);
    }

    @Override
    public Component getAction() {
        return Component.text("deleted");
    }
}

package com.ebicep.warlords.guilds.logs.types.twoplayer;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public class GuildLogKick extends AbstractGuildLogTwoPlayer {

    public GuildLogKick(UUID sender, UUID receiver) {
        super(sender, receiver);
    }

    @Override
    public Component getAction() {
        return Component.text("kicked");
    }
}

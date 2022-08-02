package com.ebicep.warlords.guilds.logs.types.twoplayer;

import java.util.UUID;

public class GuildLogKick extends AbstractGuildLogTwoPlayer {

    public GuildLogKick(UUID sender, UUID receiver) {
        super(sender, receiver);
    }

    @Override
    public String getAction() {
        return "kicked";
    }
}

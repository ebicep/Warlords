package com.ebicep.warlords.guilds.logs.types.twoplayer;

import java.util.UUID;

public class GuildLogUnmute extends AbstractGuildLogTwoPlayer {

    public GuildLogUnmute(UUID sender, UUID receiver) {
        super(sender, receiver);
    }

    @Override
    public String getAction() {
        return "unmuted";
    }

}

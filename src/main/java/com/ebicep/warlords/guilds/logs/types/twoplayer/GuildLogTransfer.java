package com.ebicep.warlords.guilds.logs.types.twoplayer;

import java.util.UUID;

public class GuildLogTransfer extends AbstractGuildLogTwoPlayer {

    public GuildLogTransfer(UUID sender, UUID receiver) {
        super(sender, receiver);
    }

    @Override
    public String getAction() {
        return "transferred the guild to";
    }

}

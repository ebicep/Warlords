package com.ebicep.warlords.guilds.logs.types.twoplayer;

import java.util.UUID;

public class GuildLogInvite extends AbstractGuildLogTwoPlayer {

    public GuildLogInvite(UUID sender, UUID receiver) {
        super(sender, receiver);
    }

    @Override
    public String getAction() {
        return "invited";
    }

}

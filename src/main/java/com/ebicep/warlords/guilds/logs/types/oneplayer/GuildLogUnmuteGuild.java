package com.ebicep.warlords.guilds.logs.types.oneplayer;

import java.util.UUID;

public class GuildLogUnmuteGuild extends AbstractGuildLogOnePlayer {

    public GuildLogUnmuteGuild(UUID sender) {
        super(sender);
    }

    @Override
    public String getAction() {
        return "unmuted the guild";
    }

}

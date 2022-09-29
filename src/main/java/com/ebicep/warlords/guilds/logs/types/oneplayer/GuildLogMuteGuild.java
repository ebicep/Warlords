package com.ebicep.warlords.guilds.logs.types.oneplayer;

import java.util.UUID;

public class GuildLogMuteGuild extends AbstractGuildLogOnePlayer {

    public GuildLogMuteGuild(UUID sender) {
        super(sender);
    }

    @Override
    public String getAction() {
        return "muted the guild";
    }

}

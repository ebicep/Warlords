package com.ebicep.warlords.guilds.logs.types.oneplayer;

import java.util.UUID;

public class GuildLogLeave extends AbstractGuildLogOnePlayer {

    public GuildLogLeave(UUID sender) {
        super(sender);
    }

    @Override
    public String getAction() {
        return "left";
    }


}

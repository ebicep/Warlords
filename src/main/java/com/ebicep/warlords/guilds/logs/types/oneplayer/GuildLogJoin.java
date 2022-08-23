package com.ebicep.warlords.guilds.logs.types.oneplayer;

import java.util.UUID;

public class GuildLogJoin extends AbstractGuildLogOnePlayer {

    public GuildLogJoin(UUID sender) {
        super(sender);
    }

    @Override
    public String getAction() {
        return "joined";
    }


}

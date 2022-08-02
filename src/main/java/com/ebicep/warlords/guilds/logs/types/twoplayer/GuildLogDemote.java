package com.ebicep.warlords.guilds.logs.types.twoplayer;

import java.util.UUID;

public class GuildLogDemote extends AbstractGuildLogChangeLevel {

    public GuildLogDemote(UUID sender, UUID receiver, String before, String after, int newLevel, int oldLevel) {
        super(sender, receiver, before, after, newLevel, oldLevel);
    }

    @Override
    public String getAction() {
        return "demoted";
    }

}

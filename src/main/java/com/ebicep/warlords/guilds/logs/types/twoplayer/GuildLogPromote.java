package com.ebicep.warlords.guilds.logs.types.twoplayer;

import java.util.UUID;

public class GuildLogPromote extends AbstractGuildLogChangeLevel {


    public GuildLogPromote(UUID sender, UUID receiver, String before, String after, int oldLevel, int newLevel) {
        super(sender, receiver, before, after, oldLevel, newLevel);
    }

    @Override
    public String getAction() {
        return "promoted";
    }

}

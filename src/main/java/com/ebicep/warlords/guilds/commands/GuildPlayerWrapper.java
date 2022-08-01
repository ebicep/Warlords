package com.ebicep.warlords.guilds.commands;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.util.java.Pair;

public class GuildPlayerWrapper {

    private final Pair<Guild, GuildPlayer> guildPlayerPair;


    public GuildPlayerWrapper(Pair<Guild, GuildPlayer> guildPlayerPair) {
        this.guildPlayerPair = guildPlayerPair;
    }

    public Guild getGuild() {
        return guildPlayerPair.getA();
    }

    public GuildPlayer getGuildPlayer() {
        return guildPlayerPair.getB();
    }
}

package com.ebicep.warlords.guilds.logs.types.twoplayer;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public class GuildLogPromote extends AbstractGuildLogChangeLevel {


    public GuildLogPromote(UUID sender, UUID receiver, String before, String after, int oldLevel, int newLevel) {
        super(sender, receiver, before, after, oldLevel, newLevel);
    }

    @Override
    public Component getAction() {
        return Component.text("promoted");
    }

}

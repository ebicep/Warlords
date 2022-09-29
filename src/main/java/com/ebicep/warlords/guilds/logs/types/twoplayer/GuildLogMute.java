package com.ebicep.warlords.guilds.logs.types.twoplayer;

import com.ebicep.warlords.guilds.GuildPlayerMuteEntry;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public class GuildLogMute extends AbstractGuildLogTwoPlayer {

    @Field("time_unit")
    private GuildPlayerMuteEntry.TimeUnit timeUnit;
    private Integer duration;

    public GuildLogMute() {
    }

    public GuildLogMute(UUID sender, UUID receiver, GuildPlayerMuteEntry.TimeUnit timeUnit) {
        super(sender, receiver);
        this.timeUnit = timeUnit;
    }

    public GuildLogMute(UUID sender, UUID receiver, GuildPlayerMuteEntry.TimeUnit timeUnit, Integer duration) {
        super(sender, receiver);
        this.timeUnit = timeUnit;
        this.duration = duration;
    }

    @Override
    public String getAction() {
        return timeUnit == GuildPlayerMuteEntry.TimeUnit.PERMANENT ? "permanently muted" : "muted";
    }

    @Override
    public String append() {
        return timeUnit == GuildPlayerMuteEntry.TimeUnit.PERMANENT ? "" : "for " + ChatColor.RED + duration + " " + timeUnit.name + (duration > 1 ? "s" : "");
    }
}

package com.ebicep.warlords.guilds.logs.types.twoplayer;

import com.ebicep.warlords.guilds.GuildPlayerMuteEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public Component getAction() {
        return Component.text(timeUnit == GuildPlayerMuteEntry.TimeUnit.PERMANENT ? "permanently muted" : "muted");
    }

    @Override
    public Component append() {
        return timeUnit == GuildPlayerMuteEntry.TimeUnit.PERMANENT ?
               Component.empty() :
               Component.text("for ").append(Component.text(duration + " " + timeUnit.name + (duration > 1 ? "s" : ""), NamedTextColor.RED));
    }
}

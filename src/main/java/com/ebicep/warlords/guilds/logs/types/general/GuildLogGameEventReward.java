package com.ebicep.warlords.guilds.logs.types.general;

import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

public class GuildLogGameEventReward extends AbstractGuildLog {

    private GameEvents event;
    @Field("event_date")
    private long eventDate;
    private int placement;
    private Map<String, Long> rewards;

    public GuildLogGameEventReward() {
    }

    public GuildLogGameEventReward(GameEvents event, long eventDate, int placement, Map<String, Long> rewards) {
        this.event = event;
        this.eventDate = eventDate;
        this.placement = placement;
        this.rewards = rewards;
    }

    @Override
    public Component getAction() {
        return Component.text("awarded");
    }

    @Override
    public Component getLog() { //Fighter's Glory #1 awarded - 50000 coins, 50000 experience
        return Component.textOfChildren(
                Component.text(" " + event.name + " Event #" + placement + " ", NamedTextColor.RED),
                Component.empty().color(NamedTextColor.YELLOW).append(getAction()),
                Component.text(" - ", NamedTextColor.DARK_GRAY),
                rewards.entrySet()
                       .stream()
                       .map(entry -> Component.textOfChildren(
                               Component.text(entry.getValue(), NamedTextColor.GREEN),
                               Component.text(" " + entry.getKey(), NamedTextColor.GOLD)
                       ))
                       .collect(Component.toComponent(Component.text(", ")))
        );

    }

}

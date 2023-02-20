package com.ebicep.warlords.guilds.logs.types.general;

import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;
import java.util.stream.Collectors;

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
    public String getAction() {
        return "awarded";
    }

    @Override
    public String getLog() { //Fighter's Glory #1 awarded - 50000 coins, 50000 experience
        return ChatColor.RED + " " + event.name + " Event #" + placement + " " + ChatColor.YELLOW + getAction() +
                ChatColor.DARK_GRAY + " - " + rewards.entrySet()
                                                     .stream()
                                                     .map(entry -> ChatColor.GREEN.toString() + entry.getValue() + " " + ChatColor.GOLD + entry.getKey())
                                                     .collect(Collectors.joining(", "));
    }

}

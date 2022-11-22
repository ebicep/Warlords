package com.ebicep.warlords.guilds.logs.types.oneplayer;

import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public class GuildLogDailyCoinBonus extends AbstractGuildLogOnePlayer {

    @Field("coins_gained")
    private long coinsGained;

    public GuildLogDailyCoinBonus(UUID sender, long coinsGained) {
        super(sender);
        this.coinsGained = coinsGained;
    }

    @Override
    public String getAction() {
        return "awarded";
    }

    @Override
    public String append() {
        return ChatColor.GREEN.toString() + coinsGained + ChatColor.GRAY + " player coins from daily bonus";
    }
}

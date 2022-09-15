package com.ebicep.warlords.guilds.logs.types.oneplayer;

import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public class GuildLogCoinsConverted extends AbstractGuildLogOnePlayer {

    @Field("coins_converted")
    private long coinsConverted;
    @Field("coins_gained")
    private long coinsGained;

    public GuildLogCoinsConverted(UUID sender, long coinsConverted, long coinsGained) {
        super(sender);
        this.coinsConverted = coinsConverted;
        this.coinsGained = coinsGained;
    }

    @Override
    public String getAction() {
        return "converted";
    }

    @Override
    public String append() {
        return ChatColor.GREEN.toString() + coinsConverted + ChatColor.GRAY + " player coins -> " + ChatColor.GREEN + coinsGained + ChatColor.GRAY +
                " guild coins";
    }
}

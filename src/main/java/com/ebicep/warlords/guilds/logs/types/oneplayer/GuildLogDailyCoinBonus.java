package com.ebicep.warlords.guilds.logs.types.oneplayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public Component getAction() {
        return Component.text("awarded");
    }

    @Override
    public Component append() {
        return Component.textOfChildren(
                Component.text(coinsGained, NamedTextColor.GREEN),
                Component.text(" player coins from daily bonus")
        );
    }
}

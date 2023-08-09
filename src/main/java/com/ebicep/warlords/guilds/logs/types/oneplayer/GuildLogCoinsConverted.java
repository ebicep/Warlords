package com.ebicep.warlords.guilds.logs.types.oneplayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public Component getAction() {
        return Component.text("converted");
    }

    @Override
    public Component append() {
        return Component.textOfChildren(
                Component.text(coinsConverted, NamedTextColor.GREEN),
                Component.text(" player coins -> "),
                Component.text(coinsGained, NamedTextColor.GREEN),
                Component.text(" guild coins")
        );
    }

    public long getCoinsGained() {
        return coinsGained;
    }
}

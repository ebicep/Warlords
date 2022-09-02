package com.ebicep.warlords.guilds.upgrades;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class GuildUpgrade {

    private GuildUpgrades upgrade;
    @Field("activation_date")
    private Instant activationDate = Instant.now();
    @Field("expiration_date")
    private Instant expirationDate;
    private int tier;

    public GuildUpgrade(GuildUpgrades upgrade, Instant expirationDate, int tier) {
        this.upgrade = upgrade;
        this.expirationDate = expirationDate;
        this.tier = tier;
    }
}

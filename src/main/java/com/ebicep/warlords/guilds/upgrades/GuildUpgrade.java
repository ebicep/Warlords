package com.ebicep.warlords.guilds.upgrades;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class GuildUpgrade {

    private GuildUpgrades upgrade;
    @Field("activation_date")
    private Instant activationDate;
    @Field("expiration_date")
    private Instant expirationDate;
    private int tier;

    public GuildUpgrade(GuildUpgrades upgrade, int tier) {
        Instant now = Instant.now();
        this.upgrade = upgrade;
        this.activationDate = now;
        if (upgrade.expirationDate != null) {
            this.expirationDate = upgrade.expirationDate.apply(now);
        }
        this.tier = tier;
    }

    public GuildUpgrades getUpgrade() {
        return upgrade;
    }

    public Instant getActivationDate() {
        return activationDate;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public int getTier() {
        return tier;
    }

}

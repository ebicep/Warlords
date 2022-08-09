package com.ebicep.warlords.guilds.upgrades.temporary;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class TempGuildUpgrade {

    @Field("activation_date")
    private Instant activationDate = Instant.now();
    private TempGuildUpgrades upgrade;
    private GuildTempUpgradeTiers tier;

    public TempGuildUpgrade(TempGuildUpgrades upgrade, GuildTempUpgradeTiers tier) {
        this.upgrade = upgrade;
        this.tier = tier;
    }

    public Instant getActivationDate() {
        return activationDate;
    }

}

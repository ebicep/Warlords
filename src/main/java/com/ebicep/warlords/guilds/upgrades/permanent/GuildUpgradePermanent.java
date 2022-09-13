package com.ebicep.warlords.guilds.upgrades.permanent;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;

import java.time.Instant;

public class GuildUpgradePermanent extends AbstractGuildUpgrade<GuildUpgradesPermanent> {

    public GuildUpgradePermanent(GuildUpgradesPermanent guildBlessings, int tier) {
        this.upgrade = guildBlessings;
        this.activationDate = Instant.now();
        this.tier = tier;
    }

}

package com.ebicep.warlords.guilds.logs.types.oneplayer.upgrades;

import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;

import java.util.UUID;

public class GuildLogUpgradePermanent extends AbstractGuildLogUpgrade<GuildUpgradesPermanent> {

    public GuildLogUpgradePermanent(UUID sender, GuildUpgradesPermanent upgrade, int tier) {
        super(sender, upgrade, tier);
    }

    @Override
    public String getAction() {
        return "upgraded";
    }

}

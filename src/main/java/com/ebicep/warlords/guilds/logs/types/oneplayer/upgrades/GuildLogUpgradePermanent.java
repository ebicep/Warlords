package com.ebicep.warlords.guilds.logs.types.oneplayer.upgrades;

import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;

public class GuildLogUpgradePermanent extends AbstractGuildLogUpgrade<GuildUpgradesPermanent> {

    public GuildLogUpgradePermanent(GuildUpgradesPermanent upgrade, int tier) {
        super(upgrade, tier);
    }

    @Override
    public String getAction() {
        return "upgraded";
    }

}

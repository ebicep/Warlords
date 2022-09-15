package com.ebicep.warlords.guilds.logs.types.oneplayer.upgrades;

import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradeTemporary;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;

public class GuildLogUpgradeTemporary extends AbstractGuildLogUpgrade<GuildUpgradesTemporary> {

    public GuildLogUpgradeTemporary(GuildUpgradesTemporary upgrade, int tier) {
        super(upgrade, tier);
    }

    @Override
    public String getAction() {
        return "purchased";
    }

}

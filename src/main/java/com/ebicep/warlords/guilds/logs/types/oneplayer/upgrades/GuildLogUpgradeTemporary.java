package com.ebicep.warlords.guilds.logs.types.oneplayer.upgrades;

import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class GuildLogUpgradeTemporary extends AbstractGuildLogUpgrade<GuildUpgradesTemporary> {

    public GuildLogUpgradeTemporary(UUID sender, GuildUpgradesTemporary upgrade, int tier) {
        super(sender, upgrade, tier);
    }

    @Override
    public Component getAction() {
        return Component.text("purchased");
    }

}

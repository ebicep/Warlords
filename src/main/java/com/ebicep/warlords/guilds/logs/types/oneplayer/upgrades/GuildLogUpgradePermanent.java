package com.ebicep.warlords.guilds.logs.types.oneplayer.upgrades;

import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class GuildLogUpgradePermanent extends AbstractGuildLogUpgrade<GuildUpgradesPermanent> {

    public GuildLogUpgradePermanent(UUID sender, GuildUpgradesPermanent upgrade, int tier) {
        super(sender, upgrade, tier);
    }

    @Override
    public Component getAction() {
        return Component.text("upgraded");
    }

}

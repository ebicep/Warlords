package com.ebicep.warlords.guilds.upgrades.temporary;

public enum TempGuildUpgrades {

    COINS_BOOST(),
    INSIGNIA_BOOST(),

    ;

    public TempGuildUpgrade createUpgrade(GuildTempUpgradeTiers tier) {
        return new TempGuildUpgrade(this, tier);
    }
}

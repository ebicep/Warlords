package com.ebicep.warlords.guilds.logs.types.oneplayer.upgrades;

import com.ebicep.warlords.guilds.logs.types.oneplayer.AbstractGuildLogOnePlayer;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradeTemporary;
import org.bukkit.ChatColor;

public abstract class AbstractGuildLogUpgrade<T extends Enum<T> & GuildUpgrade> extends AbstractGuildLogOnePlayer {

    private T upgrade;
    private int tier;

    public AbstractGuildLogUpgrade(T upgrade, int tier) {
        this.upgrade = upgrade;
        this.tier = tier;
    }

    @Override
    public String append() {
        return upgrade.getName() + " Tier " + tier;
    }

    public T getUpgrade() {
        return upgrade;
    }

    public int getTier() {
        return tier;
    }
}

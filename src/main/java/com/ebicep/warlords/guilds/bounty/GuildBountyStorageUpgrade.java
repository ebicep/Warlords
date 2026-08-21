package com.ebicep.warlords.guilds.bounty;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import org.bukkit.Material;

public enum GuildBountyStorageUpgrade implements GuildUpgrade {

    DATA;

    @Override
    public String getName() {
        return "Guild Bounty Data";
    }

    @Override
    public String getDescription() {
        return "Stores unlocked guild bounty slots and weekly progress.";
    }

    @Override
    public Material getMaterial() {
        return Material.PAPER;
    }

    @Override
    public double getValueFromTier(int tier) {
        return tier;
    }

    @Override
    public String getEffectBonusFromTier(int tier) {
        return tier + " bounty slot" + (tier == 1 ? "" : "s");
    }

    @Override
    public AbstractGuildUpgrade<?> createUpgrade(int tier) {
        return new GuildBountyData(tier);
    }
}

package com.ebicep.warlords.guilds.consumables;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import org.bukkit.Material;

public enum GuildConsumableUnlockUpgrade implements GuildUpgrade {

    DATA;

    @Override
    public String getName() {
        return "Guild Consumable Unlocks";
    }

    @Override
    public String getDescription() {
        return "Stores permanent consumable unlocks for the guild shop.";
    }

    @Override
    public Material getMaterial() {
        return Material.POTION;
    }

    @Override
    public double getValueFromTier(int tier) {
        return tier;
    }

    @Override
    public String getEffectBonusFromTier(int tier) {
        return tier + " consumable unlock" + (tier == 1 ? "" : "s");
    }

    @Override
    public AbstractGuildUpgrade<?> createUpgrade(int tier) {
        return new GuildConsumableUnlockData();
    }
}

package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import org.bukkit.ChatColor;

import java.util.LinkedHashMap;

public class CompensationReward extends AbstractReward {

    public CompensationReward() {
    }

    public CompensationReward(LinkedHashMap<Currencies, Long> rewards, String from) {
        super(rewards, from);
    }

    @Override
    public ChatColor getNameColor() {
        return ChatColor.DARK_AQUA;
    }
}

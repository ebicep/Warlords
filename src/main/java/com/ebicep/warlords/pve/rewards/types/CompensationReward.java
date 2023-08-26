package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.LinkedHashMap;

public class CompensationReward extends AbstractReward {

    public CompensationReward() {
    }

    public CompensationReward(LinkedHashMap<Spendable, Long> rewards, String from) {
        super(rewards, from);
    }

    @Override
    public TextColor getNameColor() {
        return NamedTextColor.DARK_AQUA;
    }
}

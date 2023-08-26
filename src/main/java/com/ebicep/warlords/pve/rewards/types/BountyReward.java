package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import net.kyori.adventure.text.format.TextColor;

import java.util.LinkedHashMap;

public class BountyReward extends AbstractReward {

    private Bounty bounty;

    public BountyReward() {
    }

    public BountyReward(LinkedHashMap<Spendable, Long> rewards, Bounty bounty) {
        super(rewards, bounty.create.get().getName() + " Bounty");
        this.bounty = bounty;
    }

    @Override
    public TextColor getNameColor() {
        return BountyUtils.COLOR;
    }
}

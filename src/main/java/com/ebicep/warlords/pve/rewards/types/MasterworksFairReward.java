package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import java.time.Instant;
import java.util.LinkedHashMap;

public class MasterworksFairReward extends AbstractReward {

    public MasterworksFairReward() {
    }

    public MasterworksFairReward(LinkedHashMap<Spendable, Long> rewards, Instant timeGiven, WeaponsPvE rarity) {
        this(rewards, timeGiven, rarity.name);
    }

    public MasterworksFairReward(LinkedHashMap<Spendable, Long> rewards, Instant timeGiven, String category) {
        super(rewards, "Masterworks Fair " + category);
        this.timeGiven = timeGiven;
    }

}

package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.LinkedHashMap;

public class MasterworksFairReward extends AbstractReward {

    @Field("time_given")
    private Instant timeGiven;

    public MasterworksFairReward() {
    }

    public MasterworksFairReward(LinkedHashMap<Spendable, Long> rewards, Instant timeGiven, WeaponsPvE rarity) {
        super(rewards, "Masterworks Fair " + rarity.name);
        this.timeGiven = timeGiven;
    }

    public Instant getTimeGiven() {
        return timeGiven;
    }

}

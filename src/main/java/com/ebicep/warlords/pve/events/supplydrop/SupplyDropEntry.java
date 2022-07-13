package com.ebicep.warlords.pve.events.supplydrop;

import java.time.Instant;

public class SupplyDropEntry {

    private SupplyDropRewards reward;
    private Instant time = Instant.now();

    public SupplyDropEntry(SupplyDropRewards reward) {
        this.reward = reward;
    }

    public SupplyDropRewards getReward() {
        return reward;
    }

    public Instant getTime() {
        return time;
    }
}

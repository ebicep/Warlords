package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.option.pve.rewards.PveRewards;

import java.util.LinkedHashMap;

public class AnomalyRewards extends PveRewards<AbstractAnomalyOption> {

    public AnomalyRewards(AbstractAnomalyOption pveOption) {
        super(pveOption);
    }

    @Override
    protected void storeCustomBaseCoinSummary(LinkedHashMap<String, Long> cachedBaseCoinSummary) {
    }

    @Override
    protected boolean shouldStoreInsigniaConverted() {
        return false;
    }

    @Override
    protected void storeWeaponFragmentGainInternal() {
    }

    @Override
    protected void storeIllusionShardGainInternal() {
    }
}
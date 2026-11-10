package com.ebicep.warlords.game.option.pve.darkdescent;

import com.ebicep.warlords.game.option.pve.rewards.PveRewards;

import java.util.LinkedHashMap;

public class DarkDescentRewards extends PveRewards<DarkDescentOption> {

    public DarkDescentRewards(DarkDescentOption pveOption) {
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

package com.ebicep.warlords.game.option.pve.raid;

import com.ebicep.warlords.game.option.pve.rewards.PveRewards;

import java.util.LinkedHashMap;

/**
 * Raids pay out through {@link RaidRewardCache} on completion rather than through the shared in-game reward pipeline,
 * so this exists mainly so {@link com.ebicep.warlords.game.option.pve.PveOption#getBaseListener()} has something to
 * call on a win.
 */
public class RaidRewards extends PveRewards<RaidOption> {

    public RaidRewards(RaidOption pveOption) {
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

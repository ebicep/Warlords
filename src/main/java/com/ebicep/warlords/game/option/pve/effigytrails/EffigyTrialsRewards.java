package com.ebicep.warlords.game.option.pve.effigytrails;

import com.ebicep.warlords.game.option.pve.rewards.PveRewards;

import java.util.LinkedHashMap;

public class EffigyTrialsRewards extends PveRewards<EffigyTrialOption> {

    public EffigyTrialsRewards(EffigyTrialOption pveOption) {
        super(pveOption);
    }

    @Override
    public void storeRewards() {
        super.storeRewards();
        storePouchRewards();
    }

    @Override
    public void storeCustomBaseCoinSummary(LinkedHashMap<String, Long> cachedBaseCoinSummary) {

    }

    @Override
    protected boolean shouldStoreInsigniaConverted() {
        return false;
    }

    @Override
    public void storeWeaponFragmentGainInternal() {
    }

    @Override
    protected void storeIllusionShardGainInternal() {
    }

    private void storePouchRewards() {
    }

}

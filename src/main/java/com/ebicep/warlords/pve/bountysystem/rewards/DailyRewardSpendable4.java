package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;
import java.util.Map;

public interface DailyRewardSpendable4 extends RewardSpendable {

    Map<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.COIN, 30000L);
        put(Currencies.SYNTHETIC_SHARD, 50L);
        put(Currencies.LEGEND_FRAGMENTS, 50L);
    }};

    @Override
    default Map<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

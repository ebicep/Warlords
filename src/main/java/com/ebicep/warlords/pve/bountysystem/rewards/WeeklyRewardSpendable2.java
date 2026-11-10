package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;

public interface WeeklyRewardSpendable2 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.COIN, 35000L);
        put(Currencies.SYNTHETIC_SHARD, 120L);
        put(Currencies.LEGEND_FRAGMENTS, 200L);
        put(Currencies.SUPPLY_DROP_TOKEN, 5L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

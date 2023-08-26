package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;
import java.util.Map;

public interface WeeklyRewardSpendable1 extends RewardSpendable {

    Map<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.COIN, 50000L);
        put(Currencies.SYNTHETIC_SHARD, 150L);
        put(Currencies.FAIRY_ESSENCE, 150L);
        put(Currencies.ILLUSION_SHARD, 5L);
        put(Currencies.COMMON_STAR_PIECE, 1L);
    }};

    @Override
    default Map<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

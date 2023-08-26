package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;
import java.util.Map;

public interface WeeklyRewardSpendable3 extends RewardSpendable {

    Map<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.SYNTHETIC_SHARD, 200L);
        put(Currencies.LEGEND_FRAGMENTS, 150L);
        put(Currencies.RARE_STAR_PIECE, 1L);
    }};

    @Override
    default Map<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

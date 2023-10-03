package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.SpendableRandomItem;

import java.util.LinkedHashMap;

public interface WeeklyRewardSpendable3 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.SYNTHETIC_SHARD, 200L);
        put(Currencies.LEGEND_FRAGMENTS, 150L);
        put(Currencies.RARE_STAR_PIECE, 1L);
        put(SpendableRandomItem.BETA, 1L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

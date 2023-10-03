package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.SpendableRandomItem;

import java.util.LinkedHashMap;

public interface WeeklyRewardSpendable4 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.SYNTHETIC_SHARD, 250L);
        put(Currencies.LEGEND_FRAGMENTS, 225L);
        put(Currencies.RARE_STAR_PIECE, 1L);
        put(SpendableRandomItem.GAMMA, 1L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;

import java.util.LinkedHashMap;

public interface WeeklyRewardSpendable8 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.SYNTHETIC_SHARD, 350L);
        put(SpendableRandomNewItem.LEGENDARY, 1L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }
}

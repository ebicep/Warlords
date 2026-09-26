package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;

import java.util.LinkedHashMap;

public interface DailyRewardSpendable1 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.COIN, 15000L);
        put(Currencies.SYNTHETIC_SHARD, 100L);
        put(Currencies.FAIRY_ESSENCE, 50L);
        put(SpendableRandomNewItem.RARE, 1L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

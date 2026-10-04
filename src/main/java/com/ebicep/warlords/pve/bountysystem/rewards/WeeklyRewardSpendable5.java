package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrop;

import java.util.LinkedHashMap;

public interface WeeklyRewardSpendable5 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(MobDrop.ZENITH_STAR, 1L);
        put(Currencies.SYNTHETIC_SHARD, 175L);
        put(Currencies.FAIRY_ESSENCE, 200L);
        put(Currencies.SUPPLY_DROP_TOKEN, 3L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

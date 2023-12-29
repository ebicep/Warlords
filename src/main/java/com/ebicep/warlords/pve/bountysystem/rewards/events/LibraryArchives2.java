package com.ebicep.warlords.pve.bountysystem.rewards.events;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.ExpSpendable;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.bountysystem.rewards.RewardSpendable;

import java.util.LinkedHashMap;

public interface LibraryArchives2 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.EVENT_POINTS_LIBRARY_ARCHIVES, 250_000L);
        put(ExpSpendable.SPEC, 20_000L);
        put(Currencies.SUPPLY_DROP_TOKEN, 25L);
        put(Currencies.ILLUSION_SHARD, 10L);
        put(Currencies.SKILL_BOOST_MODIFIER, 1L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

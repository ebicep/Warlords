package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;
import java.util.Map;

public interface LifetimeRewardSpendable2 extends RewardSpendable {

    Map<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.COIN, 125000L);
        put(Currencies.FAIRY_ESSENCE, 500L);
        put(Currencies.ILLUSION_SHARD, 80L);
        put(Currencies.SUPPLY_DROP_TOKEN, 15L);
        put(GuildSpendable.COIN, 2500L);
        put(GuildSpendable.EXPERIENCE, 2500L);
    }};

    @Override
    default Map<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;
import java.util.Map;

public interface LifetimeRewardSpendable3 extends RewardSpendable {

    Map<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.COIN, 80000L);
        put(Currencies.SYNTHETIC_SHARD, 4000L);
        put(Currencies.LEGEND_FRAGMENTS, 1500L);
        put(Currencies.RARE_STAR_PIECE, 3L);
        put(GuildSpendable.COIN, 5000L);
        put(GuildSpendable.EXPERIENCE, 5000L);
    }};

    @Override
    default Map<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

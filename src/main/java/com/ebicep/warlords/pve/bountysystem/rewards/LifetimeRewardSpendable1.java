package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;

public interface LifetimeRewardSpendable1 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.SYNTHETIC_SHARD, 8000L);
        put(Currencies.LEGEND_FRAGMENTS, 3500L);
        put(Currencies.CELESTIAL_BRONZE, 1L); //TODO items
        put(GuildSpendable.GUILD_COIN, 6000L);
        put(GuildSpendable.GUILD_EXPERIENCE, 6000L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

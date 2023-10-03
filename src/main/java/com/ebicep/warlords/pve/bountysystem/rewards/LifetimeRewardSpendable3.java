package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.SpendableRandomItem;

import java.util.LinkedHashMap;

public interface LifetimeRewardSpendable3 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.COIN, 80000L);
        put(Currencies.SYNTHETIC_SHARD, 4000L);
        put(Currencies.LEGEND_FRAGMENTS, 1500L);
        put(Currencies.RARE_STAR_PIECE, 3L);
        put(SpendableRandomItem.DELTA, 1L);
        put(GuildSpendable.GUILD_COIN, 5000L);
        put(GuildSpendable.GUILD_EXPERIENCE, 5000L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

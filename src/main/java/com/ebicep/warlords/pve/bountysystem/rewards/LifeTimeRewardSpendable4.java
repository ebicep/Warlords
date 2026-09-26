package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;

import java.util.LinkedHashMap;

public interface LifeTimeRewardSpendable4 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.COIN, 100000L);
        put(Currencies.SYNTHETIC_SHARD, 4000L);
        put(Currencies.LEGEND_FRAGMENTS, 4000L);
        put(Currencies.LEGENDARY_STAR_PIECE, 1L);
        put(SpendableRandomNewItem.LEGENDARY, 1L);
        put(GuildSpendable.GUILD_COIN, 8000L);
        put(GuildSpendable.GUILD_EXPERIENCE, 8000L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }
}

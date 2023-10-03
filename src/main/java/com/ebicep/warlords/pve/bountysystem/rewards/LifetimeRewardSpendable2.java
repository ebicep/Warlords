package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.SpendableRandomItem;

import java.util.LinkedHashMap;

public interface LifetimeRewardSpendable2 extends RewardSpendable {

    LinkedHashMap<Spendable, Long> REWARD = new LinkedHashMap<>() {{
        put(Currencies.COIN, 125000L);
        put(Currencies.FAIRY_ESSENCE, 500L);
        put(Currencies.ILLUSION_SHARD, 80L);
        put(Currencies.SUPPLY_DROP_TOKEN, 15L);
        put(SpendableRandomItem.DELTA, 1L);
        put(GuildSpendable.GUILD_COIN, 2500L);
        put(GuildSpendable.GUILD_EXPERIENCE, 2500L);
    }};

    @Override
    default LinkedHashMap<Spendable, Long> getCurrencyReward() {
        return REWARD;
    }

}

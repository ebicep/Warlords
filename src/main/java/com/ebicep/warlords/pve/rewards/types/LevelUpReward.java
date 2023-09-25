package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.AbstractReward;

import java.time.Instant;
import java.util.LinkedHashMap;

public class LevelUpReward extends AbstractReward {

    /**
     * @param level 1-100
     * @return The rewards for the level
     */
    public static LinkedHashMap<Spendable, Long> getRewardForLevel(int level) {
        LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
        if (level <= 50) {
            rewards.put(Currencies.COIN, 1000L);
        } else if (level <= 80) {
            rewards.put(Currencies.COIN, 1500L);
        } else if (level < 100) {
            rewards.put(Currencies.COIN, 3000L);
        } else if (level == 100) {
            rewards.put(Currencies.COIN, 10000L);
        }
        if (level % 10 != 0) {
            if (level < 40) {
                rewards.put(Currencies.SYNTHETIC_SHARD, 5L);
            } else if (level < 60) {
                rewards.put(Currencies.SYNTHETIC_SHARD, 10L);
            } else if (level < 80) {
                rewards.put(Currencies.SYNTHETIC_SHARD, 15L);
            } else if (level < 90) {
                rewards.put(Currencies.SYNTHETIC_SHARD, 20L);
            } else if (level < 100) {
                rewards.put(Currencies.SYNTHETIC_SHARD, 25L);
            }
        } else {
            rewards.put(Currencies.COIN, rewards.getOrDefault(Currencies.COIN, 0L) * 5);
            if (level == 100) {
                rewards.put(Currencies.FAIRY_ESSENCE, 80L);
                rewards.put(Currencies.SUPPLY_DROP_TOKEN, 20L);
                rewards.put(Currencies.ASCENDANT_SHARD, 1L);
            } else if (level == 90) {
                rewards.put(Currencies.FAIRY_ESSENCE, 40L);
                rewards.put(Currencies.SUPPLY_DROP_TOKEN, 10L);
            } else {
                rewards.put(Currencies.FAIRY_ESSENCE, 20L);
                rewards.put(Currencies.SUPPLY_DROP_TOKEN, 5L);
                if (level == 50) {
                    rewards.put(Currencies.SKILL_BOOST_MODIFIER, 1L);
                }
            }
        }
        return rewards;
    }

    private int level;
    private int prestige;

    public LevelUpReward() {
        super();
    }

    public LevelUpReward(LinkedHashMap<Spendable, Long> rewards, int level, int prestige) {
        super(rewards, null);
        this.level = level;
        this.prestige = prestige;
        this.timeClaimed = Instant.now();
    }

    public int getLevel() {
        return level;
    }

    public int getPrestige() {
        return prestige;
    }
}

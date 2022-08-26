package com.ebicep.warlords.pve.rewards;

import com.ebicep.warlords.util.java.Pair;

import java.time.Instant;

public class LevelUpReward extends AbstractReward {

    private int level;
    private int prestige;

    public LevelUpReward(Currencies currency, Long amount, int level, int prestige) {
        super(currency, amount, "Level Up");
        this.level = level;
        this.prestige = prestige;
        this.timeClaimed = Instant.now();
    }

    public static Pair<Currencies, Long> getRewardForLevel(int level) {
        if (level % 10 == 0) {
            if (level == 90) {
                return new Pair<>(Currencies.FAIRY_ESSENCE, 50L);
            } else if (level == 100) {
                return new Pair<>(Currencies.FAIRY_ESSENCE, 100L);
            }
            return new Pair<>(Currencies.FAIRY_ESSENCE, 20L);
        }
        if (level <= 40) {
            return new Pair<>(Currencies.SYNTHETIC_SHARD, 5L);
        } else if (level <= 60) {
            return new Pair<>(Currencies.SYNTHETIC_SHARD, 10L);
        } else if (level <= 80) {
            return new Pair<>(Currencies.SYNTHETIC_SHARD, 15L);
        } else if (level <= 90) {
            return new Pair<>(Currencies.SYNTHETIC_SHARD, 20L);
        }
        return new Pair<>(Currencies.SYNTHETIC_SHARD, 25L);
    }

    public int getLevel() {
        return level;
    }

    public int getPrestige() {
        return prestige;
    }
}

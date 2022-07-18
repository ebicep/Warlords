package com.ebicep.warlords.pve.rewards;

import com.ebicep.warlords.util.java.Pair;

import java.time.Instant;

public class LevelUpReward extends AbstractReward {

    private int level;
    private int prestige;

    public LevelUpReward(RewardTypes reward, float amount, int level, int prestige) {
        super(reward, amount, "Level Up");
        this.level = level;
        this.prestige = prestige;
        this.timeClaimed = Instant.now();
    }

    public static Pair<RewardTypes, Float> getRewardForLevel(int level) {
        if (level % 10 == 0) {
            if (level == 90) {
                return new Pair<>(RewardTypes.FAIRY_ESSENCE, 50f);
            } else if (level == 100) {
                return new Pair<>(RewardTypes.FAIRY_ESSENCE, 100f);
            }
            return new Pair<>(RewardTypes.FAIRY_ESSENCE, 20f);
        }
        if (level <= 40) {
            return new Pair<>(RewardTypes.SYNTHETIC_SHARD, 5f);
        } else if (level <= 60) {
            return new Pair<>(RewardTypes.SYNTHETIC_SHARD, 10f);
        } else if (level <= 80) {
            return new Pair<>(RewardTypes.SYNTHETIC_SHARD, 15f);
        } else if (level <= 90) {
            return new Pair<>(RewardTypes.SYNTHETIC_SHARD, 20f);
        }
        return new Pair<>(RewardTypes.SYNTHETIC_SHARD, 25f);
    }

    public int getLevel() {
        return level;
    }

    public int getPrestige() {
        return prestige;
    }
}

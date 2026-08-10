package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.rewards.types.LevelUpReward;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSpecialization {

    protected Weapons weapon = Weapons.FELFLAME_BLADE;
    @Field("skill_boost")
    protected SkillBoosts skillBoost;
    protected int prestige;
    @Field("prestige_dates")
    protected List<Instant> prestigeDates = new ArrayList<>();
    @Field("level_up_rewards")
    private List<LevelUpReward> levelUpRewards = new ArrayList<>();
    @Field("level_up_rewards_claimed")
    private int levelUpRewardsClaimed;
    private long experience;

    public DatabaseSpecialization() {

    }

    public DatabaseSpecialization(SkillBoosts skillBoost) {
        this.skillBoost = skillBoost;
    }

    public Weapons getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapons weapon) {
        this.weapon = weapon;
    }

    public SkillBoosts getSkillBoost() {
        return skillBoost;
    }

    public void setSkillBoost(SkillBoosts skillBoost) {
        this.skillBoost = skillBoost;
    }

    public int getPrestige() {
        return prestige;
    }

    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    public void addPrestige() {
        this.prestige++;
        this.prestigeDates.add(Instant.now());
        this.experience = 0;
    }

    public List<LevelUpReward> getLevelUpRewards() {
        return levelUpRewards;
    }

    public boolean hasLevelUpReward(int level, int prestige) {
        for (LevelUpReward reward : levelUpRewards) {
            if (reward.getPrestige() == prestige && reward.getLevel() == level) {
                return true;
            }
        }
        return false;
    }

    public int getLevelUpRewardsClaimed() {
        return levelUpRewardsClaimed;
    }

    public void setLevelUpRewardsClaimed(int levelUpRewardsClaimed) {
        this.levelUpRewardsClaimed = levelUpRewardsClaimed;
    }

    /**
     * Rewards are indexed sequentially across prestiges, {@code index = prestige * 100 + level}
     *
     * @return the highest reward index the player is allowed to claim
     */
    public int getMaxLevelUpRewardsClaimable() {
        return prestige * 100 + Math.min(100, ExperienceManager.getLevelFromExp(experience));
    }

    public boolean hasUnclaimedLevelUpRewards() {
        return levelUpRewardsClaimed < getMaxLevelUpRewardsClaimable();
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }
}

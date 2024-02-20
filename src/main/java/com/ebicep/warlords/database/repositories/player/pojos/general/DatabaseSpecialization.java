package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.rewards.types.LevelUpReward;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSpecialization extends AbstractDatabaseStatInformation<DatabaseGameBase, DatabaseGamePlayerBase> {

    protected Weapons weapon = Weapons.FELFLAME_BLADE;
    @Field("skill_boost")
    protected SkillBoosts skillBoost;
    protected int prestige;
    @Field("prestige_dates")
    protected List<Instant> prestigeDates = new ArrayList<>();
    @Field("level_up_rewards")
    private List<LevelUpReward> levelUpRewards = new ArrayList<>();

    public DatabaseSpecialization() {

    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        //UPDATE SPEC EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedSpec() * multiplier;
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

    public void addLevelUpReward(LevelUpReward levelUpReward) {
        this.levelUpRewards.add(levelUpReward);
    }

    public boolean hasLevelUpReward(int level, int prestige) {
        for (LevelUpReward reward : levelUpRewards) {
            if (reward.getPrestige() == prestige && reward.getLevel() == level) {
                return true;
            }
        }
        return false;
    }

}

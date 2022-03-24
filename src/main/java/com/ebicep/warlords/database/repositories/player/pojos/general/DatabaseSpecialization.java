package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.SkillBoosts;
import com.ebicep.warlords.player.Weapons;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseSpecialization extends AbstractDatabaseStatInformation {

    protected Weapons weapon = Weapons.FELFLAME_BLADE;
    @Field("skill_boost")
    protected SkillBoosts skillBoost;

    public DatabaseSpecialization() {

    }

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean isCompGame, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
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
}

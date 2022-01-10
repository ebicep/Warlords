package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.games.GameMode;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayers;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.player.ClassesSkillBoosts;
import com.ebicep.warlords.player.Weapons;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseSpecialization extends AbstractDatabaseStatInformation {

    protected Weapons weapon = Weapons.FELFLAME_BLADE;
    @Field("skill_boost")
    protected ClassesSkillBoosts skillBoost;

    public DatabaseSpecialization() {

    }

    @Override
    public void updateCustomStats(GameMode gameMode, boolean isCompGame, DatabaseGame databaseGame, DatabaseGamePlayers.GamePlayer gamePlayer, boolean won, boolean add) {
        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
    }

    public DatabaseSpecialization(ClassesSkillBoosts skillBoost) {
        this.skillBoost = skillBoost;
    }

    public Weapons getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapons weapon) {
        this.weapon = weapon;
    }

    public ClassesSkillBoosts getSkillBoost() {
        return skillBoost;
    }

    public void setSkillBoost(ClassesSkillBoosts skillBoost) {
        this.skillBoost = skillBoost;
    }
}

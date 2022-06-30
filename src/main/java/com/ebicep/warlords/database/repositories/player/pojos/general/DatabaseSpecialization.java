package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Weapons;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseSpecialization extends AbstractDatabaseStatInformation {

    protected Weapons weapon = Weapons.FELFLAME_BLADE;
    @Field("skill_boost")
    protected SkillBoosts skillBoost;
    protected int prestige;
    @Field("prestige_dates")
    protected List<Date> prestigeDates = new ArrayList<>();

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

    public int getPrestige() {
        return prestige;
    }

    public void setPrestige(int prestige) {
        this.prestige = prestige;
    }

    public void addPrestige() {
        this.prestige++;
        this.prestigeDates.add(new Date());
        this.experience = 0;
    }

}

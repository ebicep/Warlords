package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.SkillBoosts;

import java.util.List;

public class DatabaseWarrior extends DatabaseBaseGeneral {

    private DatabaseSpecialization berserker = new DatabaseSpecialization(SkillBoosts.WOUNDING_STRIKE_BERSERKER);
    private DatabaseSpecialization defender = new DatabaseSpecialization(SkillBoosts.WOUNDING_STRIKE_DEFENDER);
    private DatabaseSpecialization revenant = new DatabaseSpecialization(SkillBoosts.ORBS_OF_LIFE);

    public DatabaseWarrior() {
        super(ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET);
    }

    @Override
    public List<List<DatabaseSpecialization>> getSpecs() {
        return new DatabaseSpecialization[]{berserker, defender, revenant};
    }

    public DatabaseSpecialization getBerserker() {
        return berserker;
    }

    public DatabaseSpecialization getDefender() {
        return defender;
    }

    public DatabaseSpecialization getRevenant() {
        return revenant;
    }


}

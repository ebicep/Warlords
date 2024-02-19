package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.SkillBoosts;

public class DatabaseMage extends DatabaseBaseGeneral {

    protected DatabaseSpecialization pyromancer = new DatabaseSpecialization(SkillBoosts.FIREBALL);
    protected DatabaseSpecialization cryomancer = new DatabaseSpecialization(SkillBoosts.FROST_BOLT);
    protected DatabaseSpecialization aquamancer = new DatabaseSpecialization(SkillBoosts.WATER_BOLT);

    public DatabaseMage() {
        super(ArmorManager.Helmets.SIMPLE_MAGE_HELMET);
    }

    @Override
    public DatabaseSpecialization[] getSpecs() {
        return new DatabaseSpecialization[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseSpecialization getPyromancer() {
        return pyromancer;
    }

    public DatabaseSpecialization getCryomancer() {
        return cryomancer;
    }

    public DatabaseSpecialization getAquamancer() {
        return aquamancer;
    }

}

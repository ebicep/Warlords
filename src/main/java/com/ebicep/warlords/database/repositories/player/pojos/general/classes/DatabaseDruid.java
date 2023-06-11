
package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.SkillBoosts;

public class DatabaseDruid extends DatabaseBaseGeneral implements DatabaseWarlordsSpecs {

    private DatabaseSpecialization conjurer = new DatabaseSpecialization(SkillBoosts.WOUNDING_STRIKE_BERSERKER);
    private DatabaseSpecialization guardian = new DatabaseSpecialization(SkillBoosts.WOUNDING_STRIKE_DEFENDER);
    private DatabaseSpecialization priest = new DatabaseSpecialization(SkillBoosts.ORBS_OF_LIFE);

    public DatabaseDruid() {
        super(ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET);
    }

    @Override
    public DatabaseSpecialization[] getSpecs() {
        return new DatabaseSpecialization[]{conjurer, guardian, priest};
    }

    public DatabaseSpecialization getConjurer() {
        return conjurer;
    }

    public DatabaseSpecialization getGuardian() {
        return guardian;
    }

    public DatabaseSpecialization getPriest() {
        return priest;
    }


}

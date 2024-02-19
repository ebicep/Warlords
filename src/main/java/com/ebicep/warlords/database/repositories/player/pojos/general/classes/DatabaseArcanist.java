
package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.SkillBoosts;

public class DatabaseArcanist extends DatabaseBaseGeneral {

    private DatabaseSpecialization conjurer = new DatabaseSpecialization(SkillBoosts.POISONOUS_HEX);
    private DatabaseSpecialization sentinel = new DatabaseSpecialization(SkillBoosts.FORTIFYING_HEX);
    private DatabaseSpecialization luminary = new DatabaseSpecialization(SkillBoosts.MERCIFUL_HEX);

    public DatabaseArcanist() {
        super(ArmorManager.Helmets.SIMPLE_ARCANIST_HELMET);
    }

    @Override
    public DatabaseSpecialization[] getSpecs() {
        return new DatabaseSpecialization[]{conjurer, sentinel, luminary};
    }

    public DatabaseSpecialization getConjurer() {
        return conjurer;
    }

    public DatabaseSpecialization getSentinel() {
        return sentinel;
    }

    public DatabaseSpecialization getLuminary() {
        return luminary;
    }


}

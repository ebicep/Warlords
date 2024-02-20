package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.SkillBoosts;

public class DatabaseShaman extends DatabaseBaseGeneral {

    private DatabaseSpecialization thunderlord = new DatabaseSpecialization(SkillBoosts.LIGHTNING_BOLT);
    private DatabaseSpecialization spiritguard = new DatabaseSpecialization(SkillBoosts.FALLEN_SOULS);
    private DatabaseSpecialization earthwarden = new DatabaseSpecialization(SkillBoosts.EARTHEN_SPIKE);

    public DatabaseShaman() {
        super(ArmorManager.Helmets.SIMPLE_SHAMAN_HELMET);
    }

    public DatabaseSpecialization[] getSpecs() {
        return new DatabaseSpecialization[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseSpecialization getThunderlord() {
        return thunderlord;
    }

    public DatabaseSpecialization getSpiritguard() {
        return spiritguard;
    }

    public DatabaseSpecialization getEarthwarden() {
        return earthwarden;
    }

}

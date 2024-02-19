package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.SkillBoosts;

public class DatabaseRogue extends DatabaseBaseGeneral {

    protected DatabaseSpecialization assassin = new DatabaseSpecialization(SkillBoosts.FIREBALL);
    protected DatabaseSpecialization vindicator = new DatabaseSpecialization(SkillBoosts.FROST_BOLT);
    protected DatabaseSpecialization apothecary = new DatabaseSpecialization(SkillBoosts.WATER_BOLT);

    public DatabaseRogue() {
        super(ArmorManager.Helmets.SIMPLE_ROGUE_HELMET);
    }

    @Override
    public DatabaseSpecialization[] getSpecs() {
        return new DatabaseSpecialization[]{assassin, vindicator, apothecary};
    }

    public DatabaseSpecialization getAssassin() {
        return assassin;
    }

    public DatabaseSpecialization getVindicator() {
        return vindicator;
    }

    public DatabaseSpecialization getApothecary() {
        return apothecary;
    }

}

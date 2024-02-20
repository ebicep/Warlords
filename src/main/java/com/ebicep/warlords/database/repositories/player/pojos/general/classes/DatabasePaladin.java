package com.ebicep.warlords.database.repositories.player.pojos.general.classes;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.SkillBoosts;

import java.util.List;

public class DatabasePaladin extends DatabaseBaseGeneral {

    private DatabaseSpecialization avenger = new DatabaseSpecialization(SkillBoosts.AVENGER_STRIKE);
    private DatabaseSpecialization crusader = new DatabaseSpecialization(SkillBoosts.CRUSADER_STRIKE);
    private DatabaseSpecialization protector = new DatabaseSpecialization(SkillBoosts.PROTECTOR_STRIKE);

    public DatabasePaladin() {
        super(ArmorManager.Helmets.SIMPLE_PALADIN_HELMET);
    }

    @Override
    public List<List<DatabaseSpecialization>> getSpecs() {
        return new DatabaseSpecialization[]{avenger, crusader, protector};
    }

    public DatabaseSpecialization getAvenger() {
        return avenger;
    }

    public DatabaseSpecialization getCrusader() {
        return crusader;
    }

    public DatabaseSpecialization getProtector() {
        return protector;
    }

}
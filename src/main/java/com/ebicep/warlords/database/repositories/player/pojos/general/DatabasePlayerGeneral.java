package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.*;

public abstract class DatabasePlayerGeneral implements StatsWarlordsClasses<DatabaseSpecialization, StatsWarlordsSpecs<DatabaseSpecialization>> {

    private DatabaseMage mage = new DatabaseMage();
    private DatabaseWarrior warrior = new DatabaseWarrior();
    private DatabasePaladin paladin = new DatabasePaladin();
    private DatabaseShaman shaman = new DatabaseShaman();
    private DatabaseRogue rogue = new DatabaseRogue();
    private DatabaseArcanist arcanist = new DatabaseArcanist();


}

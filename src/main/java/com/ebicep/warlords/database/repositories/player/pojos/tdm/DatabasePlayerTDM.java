package com.ebicep.warlords.database.repositories.player.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGamePlayerTDM;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.classes.*;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerTDM implements TDMStatsWarlordsClasses {

    private DatabaseMageTDM mage = new DatabaseMageTDM();
    private DatabaseWarriorTDM warrior = new DatabaseWarriorTDM();
    private DatabasePaladinTDM paladin = new DatabasePaladinTDM();
    private DatabaseShamanTDM shaman = new DatabaseShamanTDM();
    private DatabaseRogueTDM rogue = new DatabaseRogueTDM();
    private DatabaseArcanistTDM arcanist = new DatabaseArcanistTDM();

    @Override
    public StatsWarlordsSpecs<DatabaseGameTDM, DatabaseGamePlayerTDM, TDMStats> getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case ARCANIST -> arcanist;
        };
    }
}

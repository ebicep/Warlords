package com.ebicep.warlords.database.repositories.player.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGamePlayerDuel;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.duel.classes.*;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerDuel implements DuelStatsWarlordsClasses {

    private DatabaseMageDuel mage = new DatabaseMageDuel();
    private DatabaseWarriorDuel warrior = new DatabaseWarriorDuel();
    private DatabasePaladinDuel paladin = new DatabasePaladinDuel();
    private DatabaseShamanDuel shaman = new DatabaseShamanDuel();
    private DatabaseRogueDuel rogue = new DatabaseRogueDuel();
    private DatabaseArcanistDuel arcanist = new DatabaseArcanistDuel();

    @Override
    public StatsWarlordsSpecs<DatabaseGameDuel, DatabaseGamePlayerDuel, DuelStats> getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> getMage();
            case WARRIOR -> getWarrior();
            case PALADIN -> getPaladin();
            case SHAMAN -> getShaman();
            case ROGUE -> getRogue();
            case ARCANIST -> getArcanist();
        };
    }
}

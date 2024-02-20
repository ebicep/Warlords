package com.ebicep.warlords.database.repositories.player.pojos.duel;

import com.ebicep.warlords.database.repositories.player.pojos.duel.classes.*;

public class DatabasePlayerDuel implements DuelStatsWarlordsClasses {

    private DatabaseMageDuel mage = new DatabaseMageDuel();
    private DatabaseWarriorDuel warrior = new DatabaseWarriorDuel();
    private DatabasePaladinDuel paladin = new DatabasePaladinDuel();
    private DatabaseShamanDuel shaman = new DatabaseShamanDuel();
    private DatabaseRogueDuel rogue = new DatabaseRogueDuel();
    private DatabaseArcanistDuel arcanist = new DatabaseArcanistDuel();

}

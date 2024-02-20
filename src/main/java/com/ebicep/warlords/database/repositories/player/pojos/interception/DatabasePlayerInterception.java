package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.player.pojos.interception.classes.*;

public class DatabasePlayerInterception implements InterceptionStatsWarlordsClasses {

    private DatabaseMageInterception mage = new DatabaseMageInterception();
    private DatabaseWarriorInterception warrior = new DatabaseWarriorInterception();
    private DatabasePaladinInterception paladin = new DatabasePaladinInterception();
    private DatabaseShamanInterception shaman = new DatabaseShamanInterception();
    private DatabaseRogueInterception rogue = new DatabaseRogueInterception();
    private DatabaseArcanistInterception arcanist = new DatabaseArcanistInterception();

}

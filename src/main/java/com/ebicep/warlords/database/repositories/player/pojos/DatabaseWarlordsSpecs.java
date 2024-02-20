package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;

import java.util.List;

public interface DatabaseWarlordsSpecs<DatabaseGameT extends DatabaseGameBase,
        DatabaseGamePlayerT extends DatabaseGamePlayerBase,
        T extends Stats<DatabaseGameT, DatabaseGamePlayerT>> {

    List<List<T>> getSpecs();

}

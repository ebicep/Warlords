package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStats;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;

public interface MultiStatsGeneral extends MultiStats<
        StatsWarlordsClasses<
                DatabaseGameBase<DatabaseGamePlayerBase>,
                DatabaseGamePlayerBase,
                Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>,
                StatsWarlordsSpecs<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>>>,
        DatabaseGameBase<DatabaseGamePlayerBase>,
        DatabaseGamePlayerBase,
        Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>,
        StatsWarlordsSpecs<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>>> {

}

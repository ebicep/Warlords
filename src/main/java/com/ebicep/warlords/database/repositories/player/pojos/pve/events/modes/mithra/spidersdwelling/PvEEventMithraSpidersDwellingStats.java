package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.PvEEventMithraStats;

public interface PvEEventMithraSpidersDwellingStats extends PvEEventMithraStats<
        DatabaseGamePvEEventSpidersDwelling,
        DatabaseGamePlayerPvEEventSpidersDwelling> {

    int getHighestWaveCleared();

    int getTotalWavesCleared();

}

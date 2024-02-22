package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.PvEEventMithraStatsWarlordsClasses;

public interface PvEEventMithraSpidersDwellingStatsWarlordsClasses extends PvEEventMithraStatsWarlordsClasses<
        DatabaseGamePvEEventSpidersDwelling,
        DatabaseGamePlayerPvEEventSpidersDwelling,
        PvEEventMithraSpidersDwellingStats,
        PvEEventMithraSpidersDwellingStatsWarlordsSpecs>,
        PvEEventMithraSpidersDwellingStats {

    @Override
    default int getHighestWaveCleared() {
        return getStat(PvEEventMithraSpidersDwellingStats::getHighestWaveCleared, Math::max, 0);
    }

    @Override
    default int getTotalWavesCleared() {
        return getStat(PvEEventMithraSpidersDwellingStats::getTotalWavesCleared, Integer::sum, 0);
    }
}

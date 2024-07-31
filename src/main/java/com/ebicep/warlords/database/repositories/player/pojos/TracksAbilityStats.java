package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.abilities.internal.AbilityStats;

import java.util.Map;

public interface TracksAbilityStats {

    Map<String, AbilityStats<?>> getAbilityStats();

}

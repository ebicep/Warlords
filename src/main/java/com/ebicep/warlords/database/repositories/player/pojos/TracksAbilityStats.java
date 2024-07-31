package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.abilities.internal.AbstractAbilityStats;

import java.util.Map;

public interface TracksAbilityStats {

    Map<String, AbstractAbilityStats<?>> getAbilityStats();

}

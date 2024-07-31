package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.abilities.internal.AbilityStats;

import java.util.*;

public interface TracksMultiAbilityStats extends TracksAbilityStats {

    Collection<TracksAbilityStats> getAllAbilityStats();

    default Optional<? extends AbilityStats<?>> getStat(String ability) {
        return getAllAbilityStats()
                .stream()
                .flatMap(trackAbilityStats -> trackAbilityStats.getAbilityStats().entrySet().stream())
                .filter(entry -> entry.getKey().equals(ability))
                .map(Map.Entry::getValue)
                .reduce((abilityStats, abilityStats2) -> abilityStats.merge(abilityStats.getClazz().cast(abilityStats2)));

    }

    @Override
    default Map<String, AbilityStats<?>> getAbilityStats() {
        Map<String, AbilityStats<?>> abilityStats = new HashMap<>();
        Set<String> keys = new HashSet<>();
        getAllAbilityStats().forEach(trackAbilityStats -> keys.addAll(trackAbilityStats.getAbilityStats().keySet()));
        keys.forEach(s -> getStat(s).ifPresent(stat -> abilityStats.put(s, stat)));
        return abilityStats;
    }
}

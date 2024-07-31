package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.abilities.internal.AbilityStats;

import java.util.*;

public interface TracksMultiAbilityStats extends TracksAbilityStats {

    Collection<TracksAbilityStats> getAllAbilityStats();

    default AbilityStats<?> getStat(String ability) {
        List<? extends AbilityStats<?>> abilityStats = getAllAbilityStats()
                .stream()
                .flatMap(trackAbilityStats -> trackAbilityStats.getAbilityStats().entrySet().stream())
                .filter(entry -> entry.getKey().equals(ability))
                .map(Map.Entry::getValue)
                .toList();
        if (abilityStats.isEmpty()) {
            return null;
        }
        if (abilityStats.size() == 1) {
            return abilityStats.get(0);
        }
        AbilityStats<?> merge = AbilityStats.merge(abilityStats.get(0), abilityStats.get(1));
        for (int i = 2; i < abilityStats.size(); i++) {
            merge = AbilityStats.merge(merge, abilityStats.get(i));
        }
        return merge;
    }

    @Override
    default Map<String, AbilityStats<?>> getAbilityStats() {
        Map<String, AbilityStats<?>> abilityStats = new HashMap<>();
        Set<String> keys = new HashSet<>();
        getAllAbilityStats().forEach(trackAbilityStats -> keys.addAll(trackAbilityStats.getAbilityStats().keySet()));
        keys.forEach(s -> {
            AbilityStats<?> stats = getStat(s);
            if (stats != null) {
                abilityStats.put(s, stats);
            }
        });
        return abilityStats;
    }
}

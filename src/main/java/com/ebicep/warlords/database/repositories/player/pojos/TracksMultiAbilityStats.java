package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityStats;

import java.util.*;

public interface TracksMultiAbilityStats extends TracksAbilityStats {

    Collection<TracksAbilityStats> getAllAbilityStats();

    default <T extends AbstractAbility, R extends AbstractAbilityStats<T, R>> AbstractAbilityStats<T, R> getStat(Ability<T> ability) {
        List<? extends AbstractAbilityStats<?, ?>> abilityStats = getAllAbilityStats()
                .stream()
                .flatMap(trackAbilityStats -> trackAbilityStats.getAbilityStats().entrySet().stream())
                .filter(entry -> entry.getKey().equals(ability))
                .map(Map.Entry::getValue)
                .toList();
        if (abilityStats.isEmpty()) {
            return null;
        }
        if (abilityStats.size() == 1) {
            return (AbstractAbilityStats<T, R>) abilityStats.get(0);
        }
        AbstractAbilityStats<?, ?> merge = AbstractAbilityStats.merge(abilityStats.get(0), abilityStats.get(1), 1);
        for (int i = 2; i < abilityStats.size(); i++) {
            merge = AbstractAbilityStats.merge(merge, abilityStats.get(i), 1);
        }
        return (AbstractAbilityStats<T, R>) merge;
    }

    @Override
    default Map<Ability<?>, AbstractAbilityStats<?, ?>> getAbilityStats() {
        Map<Ability<?>, AbstractAbilityStats<?, ?>> abilityStats = new HashMap<>();
        Set<Ability<?>> keys = new HashSet<>();
        getAllAbilityStats().forEach(trackAbilityStats -> keys.addAll(trackAbilityStats.getAbilityStats().keySet()));
        keys.forEach(s -> {
            AbstractAbilityStats<?, ?> stats = getStat(s);
            if (stats != null) {
                abilityStats.put(s, stats);
            }
        });
        return abilityStats;
    }

}

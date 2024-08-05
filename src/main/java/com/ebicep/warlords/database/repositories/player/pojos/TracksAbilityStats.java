package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityStats;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;

import java.util.Map;

public interface TracksAbilityStats {

    Map<Ability<?>, AbstractAbilityStats<?, ?>> getAbilityStats();

    default <T extends AbstractAbility, R extends AbstractAbilityStats<T, R>> void putAbilityStats(Ability<T> key, AbstractAbilityStats<T, R> value) {
        getAbilityStats().put(key, value);
    }

    default <T extends AbstractAbility, R extends AbstractAbilityStats<T, R>> AbstractAbilityStats<T, R> getAbilityStats(Ability<T> key) {
        return (AbstractAbilityStats<T, R>) getAbilityStats().get(key);
    }

    default void updateAbilityStats(DatabaseGamePlayerBase gamePlayerBase) {
        gamePlayerBase.getAbilityStats()
                      .forEach((ability, abstractAbilityStats) -> getAbilityStats().put(ability, AbstractAbilityStats.merge(getAbilityStats(ability), abstractAbilityStats)));
    }

}

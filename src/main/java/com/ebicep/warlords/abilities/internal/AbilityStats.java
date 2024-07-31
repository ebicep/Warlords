package com.ebicep.warlords.abilities.internal;

import org.springframework.data.mongodb.core.mapping.Field;

public abstract class AbilityStats<T extends AbilityStats<T>> {

    public static AbilityStats<?> merge(AbilityStats<?> ability1, AbilityStats<?> ability2) {
        if (ability1.getClazz().equals(ability1.getClazz())) {
            try {
                return (AbilityStats<?>) ability1
                        .getClazz()
                        .getMethod("merge", ability1.getClazz())
                        .invoke(ability1, ability1.getClazz().cast(ability2));
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot merge two different AbilityStats classes");
            }
        }
        throw new IllegalArgumentException("Cannot merge two different AbilityStats classes");
    }

    @Field("times_used")
    private int timesUsed = 0;

    /**
     * @param other other AbilityStats object
     * @return new AbilityStats object with this AbilityStats data merged with other AbilityStats data
     */
    public abstract T merge(T other);

    public abstract T unmerge(T other);

    public abstract Class<T> getClazz();

}

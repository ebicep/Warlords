package com.ebicep.warlords.abilities.internal;

import org.springframework.data.mongodb.core.mapping.Field;

public abstract class AbstractAbilityStats<T extends AbstractAbilityStats<T>> {

    public static AbstractAbilityStats<?> merge(AbstractAbilityStats<?> ability1, AbstractAbilityStats<?> ability2) {
        if (ability1.getClazz().equals(ability1.getClazz())) {
            try {
                return (AbstractAbilityStats<?>) ability1
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

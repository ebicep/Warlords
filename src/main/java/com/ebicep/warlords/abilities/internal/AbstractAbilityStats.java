package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAbilityStats<T extends AbstractAbility, R extends AbstractAbilityStats<T, R>> {

    public static AbstractAbilityStats<?, ?> merge(AbstractAbilityStats<?, ?> ability1, AbstractAbilityStats<?, ?> ability2, int multiplier) {
        if (ability1.getClazz().equals(ability1.getClazz())) {
            try {
                return (AbstractAbilityStats<?, ?>) ability1
                        .getClazz()
                        .getMethod("merge", ability1.getClazz(), int.class)
                        .invoke(ability1, ability1.getClazz().cast(ability2), multiplier);
            } catch (Exception e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Problem merging: " + ability1.getClass().getSimpleName() + " and " + ability2.getClass().getSimpleName());
                throw new IllegalArgumentException(e);
            }
        }
        ChatUtils.MessageType.WARLORDS.sendErrorMessage("Problem merging: " + ability1.getClass().getSimpleName() + " and " + ability2.getClass().getSimpleName());
        throw new IllegalArgumentException("Cannot merge two different AbilityStats classes");
    }

    public abstract Class<R> getClazz();

    @Field("times_used")
    protected int timesUsed = 0;

    public void addTimesUsed() {
        timesUsed++;
    }

    public List<AbilityStatDisplay> getStatsDisplay() {
        List<AbilityStatDisplay> statsDisplay = new ArrayList<>();
        statsDisplay.add(new AbilityStatDisplay("Times Used", timesUsed));
        return statsDisplay;
    }

    /**
     * Always database ability stats MERGE with the new ability stats
     *
     * @param other      other AbilityStats object
     * @param multiplier multiplier to apply to the other AbilityStats object
     *
     * @return new AbilityStats object with this AbilityStats data merged with other AbilityStats data
     */
    public R merge(R other, int multiplier) {
        R r = create();
        r.timesUsed = timesUsed + other.timesUsed * multiplier;
        return r;
    }

    public abstract R create();

    public record AbilityStatDisplay(String name, String value) {

        public AbilityStatDisplay(String name, int value) {
            this(name, String.valueOf(value));
        }

        public AbilityStatDisplay(String name, double value) {
            this(name, NumberFormat.addCommaAndRound(value));
        }

    }

}

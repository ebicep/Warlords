package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.NumberFormat;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAbilityStats<T extends AbstractAbility, R extends AbstractAbilityStats<T, R>> {

    public static AbstractAbilityStats<?, ?> merge(AbstractAbilityStats<?, ?> ability1, AbstractAbilityStats<?, ?> ability2) {
        if (ability1.getClazz().equals(ability1.getClazz())) {
            try {
                return (AbstractAbilityStats<?, ?>) ability1
                        .getClazz()
                        .getMethod("merge", ability1.getClazz())
                        .invoke(ability1, ability1.getClazz().cast(ability2));
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot merge two different AbilityStats classes");
            }
        }
        throw new IllegalArgumentException("Cannot merge two different AbilityStats classes");
    }

//    public List<AbilityStatDisplay> getStatsDisplay() {
//        List<AbilityStatDisplay> stats = new ArrayList<>();
//        // loop through fields for @Stat and get value
//        // recursively loop through superclasses first then add this
//        List<Class<?>> classes = new ArrayList<>();
//        Class<?> clazz = getClass();
//        while (clazz != AbstractAbilityStats.class) {
//            classes.add(0, clazz);
//            clazz = clazz.getSuperclass();
//        }
//        for (var c : classes) {
//            addStatDisplays(c, stats);
//        }
//        return stats;
//    }
//
//    private void addStatDisplays(Class<?> clazz, List<AbilityStatDisplay> stats) {
//        for (var field : clazz.getDeclaredFields()) {
//            if (!field.isAnnotationPresent(Stat.class)) {
//                continue;
//            }
//            try {
//                Object object = field.get(this);
//                String name = field.getAnnotation(Stat.class).value();
//                String value;
//                if (object instanceof Integer integer) {
//                    value = String.valueOf(integer);
//                } else if (object instanceof Double d) {
//                    value = NumberFormat.addCommaAndRound(d);
//                } else {
//                    value = object.toString();
//                }
//                stats.add(new AbilityStatDisplay(name, value));
//            } catch (IllegalAccessException e) {
//                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
//            }
//        }
//    }

    @Field("times_used")
    protected int timesUsed = 0;

    public void addTimesUsed() {
        timesUsed++;
    }

    public List<AbilityStatDisplay> getStatsDisplay() {
        List<AbilityStatDisplay> stats = new ArrayList<>();
        stats.add(new AbilityStatDisplay("Times Used", timesUsed));
        return stats;
    }

    /**
     * Always database ability stats MERGE with the new ability stats
     *
     * @param other      other AbilityStats object
     * @param multiplier multiplier to apply to the other AbilityStats object
     * @return new AbilityStats object with this AbilityStats data merged with other AbilityStats data
     */
    public R merge(R other, int multiplier) {
        R r = create();
        r.timesUsed = timesUsed + other.timesUsed * multiplier;
        return r;
    }

    public abstract Class<R> getClazz();

    public abstract R create();

    public record AbilityStatDisplay(String name, String value) {

        public AbilityStatDisplay(String name, int value) {
            this(name, String.valueOf(value));
        }

        public AbilityStatDisplay(String name, double value) {
            this(name, NumberFormat.addCommaAndRound(value));
        }
    }

    public abstract static class TestAbility extends AbstractAbility {

        public TestAbility(String name, float cooldown, float energyCost, boolean startNoCooldown) {
            super(name, cooldown, energyCost, startNoCooldown);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            return false;
        }
    }

    public abstract static class TestStats<T extends TestAbility, R extends TestStats<T, R>> extends AbstractAbilityStats<T, R> {
        protected int test = 0;

        @Override
        public R merge(R other, int multiplier) {
            R merge = super.merge(other, multiplier);
            merge.test = test + other.test * multiplier;
            return merge;
        }
    }

    public static class TestAbility2 extends TestAbility {

        public TestAbility2(String name, float cooldown, float energyCost, boolean startNoCooldown) {
            super(name, cooldown, energyCost, startNoCooldown);
        }
    }

    public static class TestStats2 extends TestStats<TestAbility2, TestStats2> {
        protected int test2 = 0;

        @Override
        public TestStats2 merge(TestStats2 other, int multiplier) {
            TestStats2 merge = super.merge(other, multiplier);
            merge.test2 = test2 + other.test2 * multiplier;
            return merge;
        }

        @Override
        public Class<TestStats2> getClazz() {
            return TestStats2.class;
        }

        @Override
        public TestStats2 create() {
            return new TestStats2();
        }
    }

//    @Target(ElementType.FIELD)
//    @Retention(RetentionPolicy.RUNTIME)
//    public @interface Stat {
//        String value();
//    }

}

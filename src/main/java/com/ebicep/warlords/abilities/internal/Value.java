package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Value {

    static void applyDamageHealing(AbstractAbility ability, Consumer<? super Value> consumer) {
        applyDamageHealing(ability, (isDamage, value) -> consumer.accept(value));
    }

    static void applyDamageHealing(AbstractAbility ability, BiConsumer<Boolean, ? super Value> consumer) {
        if (ability instanceof Damages<?> damages) {
            ValueHolder damageValues = damages.getDamageValues();
            damageValues.getValues().forEach(value -> consumer.accept(true, value));
        }
        if (ability instanceof Heals<?> heals) {
            ValueHolder healValues = heals.getHealValues();
            healValues.getValues().forEach(value -> consumer.accept(false, value));
        }
    }

    void tick();

    default List<List<Component>> getDebugInfos() {
        return getAllValues().stream().map(FloatModifiable::getDebugInfo).toList();
    }

    /**
     * @return min/max/set value
     */
    List<FloatModifiable> getValues();

    /**
     * @return min/max/set/crit chance/crit multiplier value
     */
    default List<FloatModifiable> getAllValues() {
        return getValues();
    }

    default void forEachValue(Consumer<FloatModifiable> consumer) {
        getValues().forEach(consumer);
    }

    default void forEachAllValues(Consumer<FloatModifiable> consumer) {
        getAllValues().forEach(consumer);
    }

    interface ValueHolder {

        List<Value> getValues();

    }

    class RangedValue implements Value {

        private final FloatModifiable min;
        private final FloatModifiable max;

        public RangedValue(float min, float max) {
            this(new FloatModifiable(min), new FloatModifiable(max));
        }

        public RangedValue(FloatModifiable min, FloatModifiable max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public void tick() {
            min.tick();
            max.tick();
        }

        @Override
        public List<FloatModifiable> getValues() {
            return List.of(min, max);
        }

        public FloatModifiable min() {
            return min;
        }

        public FloatModifiable max() {
            return max;
        }

        public float getMinValue() {
            return min.getCalculatedValue();
        }

        public float getMaxValue() {
            return max.getCalculatedValue();
        }

    }

    final class RangedValueCritable extends RangedValue {

        private final FloatModifiable critChance;
        private final FloatModifiable critMultiplier;

        public RangedValueCritable(float min, float max, float critChance, float critMultiplier) {
            super(min, max);
            this.critChance = new FloatModifiable(critChance);
            this.critMultiplier = new FloatModifiable(critMultiplier);
        }

        @Override
        public List<FloatModifiable> getAllValues() {
            return List.of(min(), max(), critChance, critMultiplier);
        }

        public FloatModifiable critChance() {
            return critChance;
        }

        public FloatModifiable critMultiplier() {
            return critMultiplier;
        }

        public float getCritChanceValue() {
            return critChance.getCalculatedValue();
        }

        public float getCritMultiplierValue() {
            return critMultiplier.getCalculatedValue();
        }

    }

    record SetValue(FloatModifiable value) implements Value {

        public SetValue(float value) {
            this(new FloatModifiable(value));
        }

        @Override
        public void tick() {
            value.tick();
        }

        @Override
        public List<FloatModifiable> getValues() {
            return List.of(value);
        }

        public float getValue() {
            return value.getCalculatedValue();
        }

        public float getMultiplicativePercent() {
            return getValue() / 100f;
        }

    }

}
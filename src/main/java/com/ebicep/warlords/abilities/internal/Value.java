package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public interface Value {

    void tick();

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

        public SetValue(FloatModifiable value) {
            this.value = value;
        }

        public SetValue(float value) {
            this(new FloatModifiable(value));
        }

        @Override
        public void tick() {
            value.tick();
        }

        public float getValue() {
            return value.getCalculatedValue();
        }

        public float getMultiplicativePercent() {
            return getValue() / 100f;
        }

    }

}
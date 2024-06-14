package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public interface Value {

    void tick();

    interface ValueHolder {

        List<Value> getValues();

    }

    record RangedValue(FloatModifiable min, FloatModifiable max) implements Value {

        public RangedValue(float min, float max) {
            this(new FloatModifiable(min), new FloatModifiable(max));
        }

        @Override
        public void tick() {
            min.tick();
            max.tick();
        }

    }

    record SetValue(FloatModifiable value) implements Value {

        public SetValue(FloatModifiable value) {
            this.value = value;
        }

        @Override
        public void tick() {
            value.tick();
        }

    }

}
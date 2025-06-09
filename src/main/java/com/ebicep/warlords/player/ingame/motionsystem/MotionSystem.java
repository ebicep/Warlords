package com.ebicep.warlords.player.ingame.motionsystem;

import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.NewValueModifier;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.RemovalCondition;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MotionSystem {

    private final List<MotionModifier> modifiers = new LinkedList<>();
    private final List<Consumer<Float>> onChangeListeners = new ArrayList<>();
    private float lastValue = 0;

    public void tick() {
        modifiers.forEach(MotionModifier::tick);
        modifiers.removeIf(modifier ->
                modifier.getAddons()
                        .stream()
                        .filter(RemovalCondition.class::isInstance)
                        .map(RemovalCondition.class::cast)
                        .allMatch(RemovalCondition::removeAllMatch) ||
                        modifier.getAddons()
                                .stream()
                                .filter(RemovalCondition.class::isInstance)
                                .map(RemovalCondition.class::cast)
                                .anyMatch(RemovalCondition::removeAnyMatch)
        );
        float newValue = getNewValue();
        if (newValue != lastValue) {
            lastValue = newValue;
            onChangeListeners.forEach(listener -> listener.accept(newValue));
        }
    }

    private float getNewValue() {
        MotionModifier minModifier = null;
        MotionModifier maxModifier = null;
        for (MotionModifier modifier : modifiers) {
            float value = modifier.getModifier();
            if (value < 0 && (minModifier == null || value < minModifier.getModifier())) {
                minModifier = modifier;
            } else if (value > 0 && (maxModifier == null || value > maxModifier.getModifier())) {
                maxModifier = modifier;
            }
        }
        float min = 1 + (minModifier != null ? minModifier.getModifier() / 100 : 0);
        float max = 1 - (maxModifier != null ? -maxModifier.getModifier() / 100 : 0);
        FloatModifiable newValue = new FloatModifiable(max * min);
        NewValueData newValueData = new NewValueData(newValue, min, max);
        for (MotionModifier modifier : modifiers) {
            List<NewValueModifier> newValueModifiers = modifier
                    .getAddons()
                    .stream()
                    .filter(NewValueModifier.class::isInstance)
                    .map(NewValueModifier.class::cast)
                    .sorted(NewValueModifier::compareTo)
                    .toList();
            boolean skipOthers = false;
            for (NewValueModifier changeMultiplier : newValueModifiers) {
                if (changeMultiplier.skipIfNotMinMax() && modifier != minModifier && modifier != maxModifier) {
                    continue;
                }
                if (skipOthers && !changeMultiplier.forceApply()) {
                    continue;
                }
                changeMultiplier.modifyNewValue(newValueData);
                if (changeMultiplier.skipOthers()) {
                    skipOthers = true;
                }
            }
        }
        newValue.refresh();
        return newValue.getCalculatedValue();
    }

    public void addModifier(MotionModifier mod) {
        modifiers.removeIf(modifier -> modifier.getName().equalsIgnoreCase(mod.getName()));
        modifiers.add(mod);
    }

    public float getLastValue() {
        return lastValue;
    }

    public void removeNegativeModifiers() {
        this.modifiers.removeIf(modifier -> modifier.getModifier() < 0);
    }

    public void removeModifier(String name) {
        this.modifiers.removeIf(modifier -> modifier.getName().equals(name));
    }

    public void addBaseModifier(float add) {
        modifyBase(motionModifier -> motionModifier.setModifier(motionModifier.getModifier() + add));
    }

    public void modifyBase(Consumer<MotionModifier> modifierConsumer) {
        for (MotionModifier modifier : this.modifiers) {
            if (modifier.getName().equals("BASE")) {
                modifierConsumer.accept(modifier);
                return;
            }
        }
    }

    public List<MotionModifier> getModifiers() {
        return modifiers;
    }

    public void addChangeListener(Consumer<Float> listener) {
        onChangeListeners.add(listener);
    }

    public static final class NewValueData {

        private final FloatModifiable newValue;
        private float min;
        private float max;

        public NewValueData(FloatModifiable newValue, float min, float max) {
            this.newValue = newValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return "NewValueData[" +
                    "newValue=" + newValue + ", " +
                    "min=" + min + ", " +
                    "max=" + max + ']';
        }

        public FloatModifiable newValue() {
            return newValue;
        }

        public float min() {
            return min;
        }

        public float max() {
            return max;
        }

        public float getMin() {
            return min;
        }

        public void setMin(float min) {
            this.min = min;
        }

        public float getMax() {
            return max;
        }

        public void setMax(float max) {
            this.max = max;
        }

    }

}

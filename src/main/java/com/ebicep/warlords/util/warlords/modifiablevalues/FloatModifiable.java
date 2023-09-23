package com.ebicep.warlords.util.warlords.modifiablevalues;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FloatModifiable {

    private float currentValue;
    private final List<FloatModifier> additiveModifier = new ArrayList<>();
    private final List<FloatModifier> multiplicativeModifierAdditive = new ArrayList<>(); // these modifiers are added together
    private final List<FloatModifier> multiplicativeModifierMultiplicative = new ArrayList<>(); // these modifiers are multiplied together
    private float cachedCalculatedValue = 0;
    private float cachedAdditiveModifer = 0;
    private float cachedMultiplicativeModiferAdditive = 1;
    private float cachedMultiplicativeModiferMultiplicative = 1;

    public FloatModifiable(float currentValue) {
        this.currentValue = currentValue;
        refresh();
    }

    private void refresh() {
        cachedAdditiveModifer = (float) additiveModifier
                .stream()
                .mapToDouble(FloatModifier::getModifier)
                .sum();
        cachedMultiplicativeModiferAdditive = 1 + (float) multiplicativeModifierAdditive
                .stream()
                .mapToDouble(FloatModifier::getModifier)
                .sum();
        cachedMultiplicativeModiferMultiplicative = (float) multiplicativeModifierMultiplicative
                .stream()
                .mapToDouble(FloatModifier::getModifier)
                .reduce(1, (a, b) -> a * b);
        cachedCalculatedValue = (currentValue + cachedAdditiveModifer) * cachedMultiplicativeModiferAdditive * cachedMultiplicativeModiferMultiplicative;
    }

    public void tick() {
        AtomicBoolean dirty = new AtomicBoolean(false);
        tickModifiers(additiveModifier, dirty);
        tickModifiers(multiplicativeModifierAdditive, dirty);
        tickModifiers(multiplicativeModifierMultiplicative, dirty);
        if (dirty.get()) {
            refresh();
        }
    }

    private void tickModifiers(List<FloatModifier> modifiers, AtomicBoolean dirty) {
        modifiers.removeIf(floatModifier -> {
            if (floatModifier.tick()) {
                dirty.set(true);
                return true;
            }
            if (floatModifier.isDirty()) {
                dirty.set(true);
            }
            return false;
        });
    }

    public float getCalculatedValue() {
        return cachedCalculatedValue;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = currentValue;
        refresh();
    }

    public FloatModifier addAdditiveModifier(String log, float additiveModifier) {
        FloatModifier modifier = new FloatModifier(log, additiveModifier);
        this.additiveModifier.add(modifier);
        refresh();
        return modifier;
    }

    public FloatModifier addAdditiveModifier(String log, float additiveModifier, int ticksLeft) {
        FloatModifier modifier = new FloatModifier(log, additiveModifier, ticksLeft);
        this.additiveModifier.add(modifier);
        refresh();
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierAdd(String log, float multiplicativeModifier) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier);
        this.multiplicativeModifierAdditive.add(modifier);
        refresh();
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierAdd(String log, float multiplicativeModifier, int ticksLeft) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier, ticksLeft);
        this.multiplicativeModifierAdditive.add(modifier);
        refresh();
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierMult(String log, float multiplicativeModifier) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier);
        this.multiplicativeModifierMultiplicative.add(modifier);
        refresh();
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierMult(String log, float multiplicativeModifier, int ticksLeft) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier, ticksLeft);
        this.multiplicativeModifierMultiplicative.add(modifier);
        refresh();
        return modifier;
    }

    public static class FloatModifier {

        private final String log;
        private float modifier;
        private int ticksLeft;
        private boolean dirty = false;

        public FloatModifier(String log, float modifier, int ticksLeft) {
            this.log = log;
            this.modifier = modifier;
            this.ticksLeft = ticksLeft;
        }

        public FloatModifier(String log, float modifier) {
            this.log = log;
            this.modifier = modifier;
            this.ticksLeft = -1;
        }

        public boolean tick() {
            if (ticksLeft == -1) {
                return false; // -1 means infinite
            }
            ticksLeft--;
            return ticksLeft <= 0;
        }

        public String getLog() {
            return log;
        }

        public float getModifier() {
            return modifier;
        }

        public void setModifier(float modifier) {
            this.modifier = modifier;
            dirty = true;
        }

        public int getTicksLeft() {
            return ticksLeft;
        }

        public void forceEnd() {
            ticksLeft = 0;
        }

        public boolean isDirty() {
            boolean d = dirty;
            dirty = false;
            return d;
        }
    }

}

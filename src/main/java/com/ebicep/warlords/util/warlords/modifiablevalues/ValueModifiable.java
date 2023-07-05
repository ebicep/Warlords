package com.ebicep.warlords.util.warlords.modifiablevalues;

public abstract class ValueModifiable<T extends Number> {

    protected T currentValue;
    protected float additiveModifier = 0;
    protected float multiplicativeModifier = 1;

    public ValueModifiable(T currentValue) {
        this.currentValue = currentValue;
    }

    public T getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(T currentValue) {
        this.currentValue = currentValue;
    }

    public float getAdditiveModifier() {
        return additiveModifier;
    }

    public void setAdditiveModifier(float additiveModifier) {
        this.additiveModifier = additiveModifier;
    }

    public float getMultiplicativeModifier() {
        return multiplicativeModifier;
    }


    /**
     * If you want 40% reduction, set to 0.6
     *
     * @param multiplicativeModifier 1 is no change, 0.5 is half, 2 is double
     */
    public void setMultiplicativeModifier(float multiplicativeModifier) {
        this.multiplicativeModifier = multiplicativeModifier;
    }
}

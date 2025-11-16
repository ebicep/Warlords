package com.ebicep.warlords.util.warlords.modifiablevalues.filters;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiableFilter;

public abstract class AbstractFilter implements FloatModifiableFilter {

    private float cachedValue;
    private float cachedAdditiveModifier;
    private float cachedMultiplicativeModifierAdditive;
    private float cachedMultiplicativeModifierMultiplicative;

    public AbstractFilter() {
    }

    public AbstractFilter(float cachedValue, float cachedAdditiveModifier, float cachedMultiplicativeModifierAdditive, float cachedMultiplicativeModifierMultiplicative) {
        this.cachedValue = cachedValue;
        this.cachedAdditiveModifier = cachedAdditiveModifier;
        this.cachedMultiplicativeModifierAdditive = cachedMultiplicativeModifierAdditive;
        this.cachedMultiplicativeModifierMultiplicative = cachedMultiplicativeModifierMultiplicative;
    }

    @Override
    public abstract AbstractFilter clone();

    @Override
    public float getCachedValue() {
        return cachedValue;
    }

    @Override
    public void setCachedValue(float newValue) {
        cachedValue = newValue;
    }

    @Override
    public float getCachedAdditiveModifier() {
        return cachedAdditiveModifier;
    }

    @Override
    public void setCachedAdditiveModifier(float newValue) {
        cachedAdditiveModifier = newValue;
    }

    @Override
    public float getCachedMultiplicativeModifierAdditive() {
        return cachedMultiplicativeModifierAdditive;
    }

    @Override
    public void setCachedMultiplicativeModifierAdditive(float newValue) {
        cachedMultiplicativeModifierAdditive = newValue;
    }

    @Override
    public float getCachedMultiplicativeModifierMultiplicative() {
        return cachedMultiplicativeModifierMultiplicative;
    }

    @Override
    public void setCachedMultiplicativeModifierMultiplicative(float newValue) {
        cachedMultiplicativeModifierMultiplicative = newValue;
    }

}

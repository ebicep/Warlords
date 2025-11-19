package com.ebicep.warlords.util.warlords.modifiablevalues.filters;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class HealthFilter extends AbstractFilter {

    public HealthFilter() {
    }

    public HealthFilter(float cachedValue, float cachedAdditiveModifier, float cachedMultiplicativeModifierAdditive, float cachedMultiplicativeModifierMultiplicative) {
        super(cachedValue, cachedAdditiveModifier, cachedMultiplicativeModifierAdditive, cachedMultiplicativeModifierMultiplicative);
    }

    @Override
    public AbstractFilter clone() {
        return new HealthFilter(getCachedValue(),
                getCachedAdditiveModifier(),
                getCachedMultiplicativeModifierAdditive(),
                getCachedMultiplicativeModifierMultiplicative()
        );
    }

    @Override
    public String getName() {
        return "Max Base Health";
    }

    @Override
    public boolean overridingFilter(FloatModifiable.FloatModifier floatModifier) {
        return floatModifier.getLog().contains("(Base)");
    }

    @Override
    public boolean additiveFilter(FloatModifiable.FloatModifier floatModifier) {
        return floatModifier.getLog().contains("(Base)");
    }

    @Override
    public boolean multiplicativeAdditiveFilter(FloatModifiable.FloatModifier floatModifier) {
        return floatModifier.getLog().contains("(Base)");
    }

    @Override
    public boolean multiplicativeMultiplicativeFilter(FloatModifiable.FloatModifier floatModifier) {
        return floatModifier.getLog().contains("(Base)");
    }

}

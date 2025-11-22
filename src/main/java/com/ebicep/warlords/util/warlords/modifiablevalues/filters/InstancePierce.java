package com.ebicep.warlords.util.warlords.modifiablevalues.filters;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

// filter out cooldowns that reduce damage taken
public class InstancePierce extends AbstractFilter {

    public InstancePierce() {
    }

    public InstancePierce(float cachedValue, float cachedAdditiveModifier, float cachedMultiplicativeModifierAdditive, float cachedMultiplicativeModifierMultiplicative) {
        super(cachedValue, cachedAdditiveModifier, cachedMultiplicativeModifierAdditive, cachedMultiplicativeModifierMultiplicative);
    }

    @Override
    public AbstractFilter clone() {
        return new InstancePierce(getCachedValue(),
                getCachedAdditiveModifier(),
                getCachedMultiplicativeModifierAdditive(),
                getCachedMultiplicativeModifierMultiplicative()
        );
    }

    @Override
    public String getName() {
        return "Piercing Damage";
    }

    @Override
    public boolean overridingFilter(FloatModifiable.FloatModifier floatModifier) {
        return floatModifier.getModifier() > getCachedValue();
    }

    @Override
    public boolean additiveFilter(FloatModifiable.FloatModifier floatModifier) {
        return floatModifier.getModifier() > 0;
    }

    @Override
    public boolean multiplicativeAdditiveFilter(FloatModifiable.FloatModifier floatModifier) {
        return floatModifier.getModifier() > 0;
    }

    @Override
    public boolean multiplicativeMultiplicativeFilter(FloatModifiable.FloatModifier floatModifier) {
        return floatModifier.getModifier() > 1;
    }

}

package com.ebicep.warlords.util.warlords.modifiablevalues.filters;

public class BaseFilter extends AbstractFilter {

    public BaseFilter() {
    }

    public BaseFilter(float cachedValue, float cachedAdditiveModifier, float cachedMultiplicativeModifierAdditive, float cachedMultiplicativeModifierMultiplicative) {
        super(cachedValue, cachedAdditiveModifier, cachedMultiplicativeModifierAdditive, cachedMultiplicativeModifierMultiplicative);
    }

    @Override
    public AbstractFilter clone() {
        return new BaseFilter(getCachedValue(),
                getCachedAdditiveModifier(),
                getCachedMultiplicativeModifierAdditive(),
                getCachedMultiplicativeModifierMultiplicative()
        );
    }

    @Override
    public String getName() {
        return "Base";
    }

}

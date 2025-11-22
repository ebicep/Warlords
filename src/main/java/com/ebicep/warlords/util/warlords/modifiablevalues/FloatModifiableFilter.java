package com.ebicep.warlords.util.warlords.modifiablevalues;

public interface FloatModifiableFilter {

    String getName();

    float getCachedValue();

    void setCachedValue(float newValue);

    default float getCachedAdditiveModifier() {
        return 0;
    }

    default void setCachedAdditiveModifier(float newValue) {
    }

    default float getCachedMultiplicativeModifierAdditive() {
        return 0;
    }

    default void setCachedMultiplicativeModifierAdditive(float newValue) {
    }

    default float getCachedMultiplicativeModifierMultiplicative() {
        return 0;
    }

    default void setCachedMultiplicativeModifierMultiplicative(float newValue) {
    }

    default boolean overridingFilter(FloatModifiable.FloatModifier floatModifier) {
        return true;
    }

    default boolean additiveFilter(FloatModifiable.FloatModifier floatModifier) {
        return true;
    }

    default boolean multiplicativeAdditiveFilter(FloatModifiable.FloatModifier floatModifier) {
        return true;
    }

    default boolean multiplicativeMultiplicativeFilter(FloatModifiable.FloatModifier floatModifier) {
        return true;
    }

    FloatModifiableFilter clone();

}

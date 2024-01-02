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

    abstract class AbstractFilter implements FloatModifiableFilter {

        private float cachedValue;
        private float cachedAdditiveModifier;
        private float cachedMultiplicativeModifierAdditive;
        private float cachedMultiplicativeModifierMultiplicative;

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


    class BaseFilter extends AbstractFilter {

        @Override
        public String getName() {
            return "Base";
        }

    }

    class HealthFilter extends AbstractFilter {

        @Override
        public String getName() {
            return "Base";
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

}

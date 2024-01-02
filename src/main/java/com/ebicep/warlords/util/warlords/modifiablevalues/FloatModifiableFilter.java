package com.ebicep.warlords.util.warlords.modifiablevalues;

public interface FloatModifiableFilter {

    String getName();

    float getCachedValue();

    void setCachedValue(float newValue);

    default boolean overridingFilter(FloatModifiable.FloatModifier floatModifier) {
        return false;
    }

    default boolean additiveFilter(FloatModifiable.FloatModifier floatModifier) {
        return false;
    }

    default boolean multiplicativeAdditiveFilter(FloatModifiable.FloatModifier floatModifier) {
        return false;
    }

    default boolean multiplicativeMultiplicativeFilter(FloatModifiable.FloatModifier floatModifier) {
        return false;
    }

    class BaseFilter implements FloatModifiableFilter {

        private float cachedValue;

        @Override
        public String getName() {
            return "Base";
        }

        @Override
        public float getCachedValue() {
            return cachedValue;
        }

        @Override
        public void setCachedValue(float newValue) {
            cachedValue = newValue;
        }

    }

    class HealthFilter implements FloatModifiableFilter {

        private float cachedValue;

        @Override
        public String getName() {
            return "Base";
        }

        @Override
        public float getCachedValue() {
            return cachedValue;
        }

        @Override
        public void setCachedValue(float newValue) {
            cachedValue = newValue;
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

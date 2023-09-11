package com.ebicep.warlords.util.warlords.modifiablevalues;

public class IntModifiable extends ValueModifiable<Integer> {

    public IntModifiable(Integer currentValue) {
        super(currentValue);
    }

    public Integer getCalculatedValue() {
        return (int) ((getCurrentValue() + getAdditiveModifier()) * getMultiplicativeModifier());
    }

}

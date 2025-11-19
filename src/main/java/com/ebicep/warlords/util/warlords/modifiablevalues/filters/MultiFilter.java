package com.ebicep.warlords.util.warlords.modifiablevalues.filters;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiableFilter;

import java.util.ArrayList;
import java.util.List;

public class MultiFilter extends AbstractFilter {

    private final List<FloatModifiableFilter> filters = new ArrayList<>();

    public MultiFilter(List<FloatModifiableFilter> filters) {
        this.filters.addAll(filters);
    }

    @Override
    public String getName() {
        return "MultiFilter";
    }

    @Override
    public boolean overridingFilter(FloatModifiable.FloatModifier floatModifier) {
        for (FloatModifiableFilter filter : filters) {
            if (!filter.overridingFilter(floatModifier)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean additiveFilter(FloatModifiable.FloatModifier floatModifier) {
        for (FloatModifiableFilter filter : filters) {
            if (!filter.additiveFilter(floatModifier)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean multiplicativeAdditiveFilter(FloatModifiable.FloatModifier floatModifier) {
        for (FloatModifiableFilter filter : filters) {
            if (!filter.multiplicativeAdditiveFilter(floatModifier)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean multiplicativeMultiplicativeFilter(FloatModifiable.FloatModifier floatModifier) {
        for (FloatModifiableFilter filter : filters) {
            if (!filter.multiplicativeMultiplicativeFilter(floatModifier)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public AbstractFilter clone() {
        return new MultiFilter(filters.stream().map(FloatModifiableFilter::clone).toList());
    }

    public void addFilter(FloatModifiableFilter filter) {
        this.filters.add(filter);
    }

}

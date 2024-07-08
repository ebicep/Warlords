package com.ebicep.warlords.abilities.internal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.function.UnaryOperator;

public interface Heals<T extends Value.ValueHolder> {

    static Component formatHealing(Value.RangedValue rangedValue) {
        return AbstractAbility.formatRange(
                rangedValue.getMinValue(),
                rangedValue.getMaxValue(),
                NamedTextColor.GREEN
        );
    }

    static Component formatHealing(Value.SetValue setValue) {
        return Component.text(AbstractAbility.format(setValue.getValue()), NamedTextColor.GREEN);
    }

    static Component formatHealingPercent(Value.SetValue setValue) {
        return Component.text(AbstractAbility.format(setValue.getValue()) + "%", NamedTextColor.GREEN);
    }

    static Component formatHealingPercent(Value.SetValue setValue, UnaryOperator<Float> operator) {
        return Component.text(AbstractAbility.format(operator.apply(setValue.getValue())) + "%", NamedTextColor.GREEN);
    }

    T getHealValues();

}

package com.ebicep.warlords.abilities.internal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface Heals<T extends Value.ValueHolder> {

    T getHealValues();

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

}

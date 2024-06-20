package com.ebicep.warlords.abilities.internal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface Damages<T extends Value.ValueHolder> {

    T getDamageValues();

    static Component formatDamage(Value.RangedValue rangedValue) {
        return AbstractAbility.formatRange(
                rangedValue.getMinValue(),
                rangedValue.getMaxValue(),
                NamedTextColor.RED
        );
    }

    static Component formatDamage(Value.SetValue setValue) {
        return Component.text(AbstractAbility.format(setValue.getValue()), NamedTextColor.RED);
    }

}

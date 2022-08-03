package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;

import java.util.UUID;
import java.util.function.Function;

public enum LegendaryTitles {

    TITANIC(LegendaryTitanic::new),
    VIGOROUS(LegendaryVigorous::new),
    SUSPICIOUS(LegendarySuspicious::new),
    BENEVOLENT(LegendaryBenevolent::new),
    VORPAL(LegendaryVorpal::new),
    DIVINE(LegendaryDivine::new),

    ;

    public final Function<UUID, AbstractLegendaryWeapon> create;

    LegendaryTitles(Function<UUID, AbstractLegendaryWeapon> create) {
        this.create = create;
    }
}

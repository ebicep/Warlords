package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import java.util.UUID;
import java.util.function.Function;

public enum LegendaryTitles {

    TITANIC("Titanic", LegendaryTitanic::new, LegendaryTitanic::new),
    VIGOROUS("Vigorous", LegendaryVigorous::new, LegendaryVigorous::new),
    SUSPICIOUS("Suspicious", LegendarySuspicious::new, LegendarySuspicious::new),
    BENEVOLENT("Benevolent", LegendaryBenevolent::new, LegendaryBenevolent::new),
    VORPAL("Vorpal", LegendaryVorpal::new, LegendaryVorpal::new),
    DIVINE("Divine", LegendaryDivine::new, LegendaryDivine::new),

    ;


    public static final LegendaryTitles[] VALUES = values();

    public final String title;
    public final Function<UUID, AbstractLegendaryWeapon> create;
    public final Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon;

    LegendaryTitles(String title, Function<UUID, AbstractLegendaryWeapon> create, Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon) {
        this.title = title;
        this.create = create;
        this.titleWeapon = titleWeapon;
    }
}

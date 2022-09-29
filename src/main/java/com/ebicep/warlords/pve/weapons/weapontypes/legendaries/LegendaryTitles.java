package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import java.util.UUID;
import java.util.function.Function;

public enum LegendaryTitles {

    TITANIC("Titanic", LegendaryTitanic.class, LegendaryTitanic::new, LegendaryTitanic::new),
    VIGOROUS("Vigorous", LegendaryVigorous.class, LegendaryVigorous::new, LegendaryVigorous::new),
    SUSPICIOUS("Suspicious", LegendarySuspicious.class, LegendarySuspicious::new, LegendarySuspicious::new),
    BENEVOLENT("Benevolent", LegendaryBenevolent.class, LegendaryBenevolent::new, LegendaryBenevolent::new),
    VORPAL("Vorpal", LegendaryVorpal.class, LegendaryVorpal::new, LegendaryVorpal::new),
    DIVINE("Divine", LegendaryDivine.class, LegendaryDivine::new, LegendaryDivine::new),

    ;


    public static final LegendaryTitles[] VALUES = values();

    public final String title;
    public final Class<?> clazz;
    public final Function<UUID, AbstractLegendaryWeapon> create;
    public final Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon;

    LegendaryTitles(
            String title,
            Class<?> clazz,
            Function<UUID, AbstractLegendaryWeapon> create,
            Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon
    ) {
        this.title = title;
        this.clazz = clazz;
        this.create = create;
        this.titleWeapon = titleWeapon;
    }
}

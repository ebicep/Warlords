package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles.*;

import java.util.UUID;
import java.util.function.Function;

public enum LegendaryTitles {

    NONE("", LegendaryWeapon.class, LegendaryWeapon::new, LegendaryWeapon::new, 0),
    TITANIC("Titanic", LegendaryTitanic.class, LegendaryTitanic::new, LegendaryTitanic::new, 1),
    VIGOROUS("Vigorous", LegendaryVigorous.class, LegendaryVigorous::new, LegendaryVigorous::new, 2),
    SUSPICIOUS("Suspicious", LegendarySuspicious.class, LegendarySuspicious::new, LegendarySuspicious::new, 3),
    BENEVOLENT("Benevolent", LegendaryBenevolent.class, LegendaryBenevolent::new, LegendaryBenevolent::new, 4),
    VORPAL("Vorpal", LegendaryVorpal.class, LegendaryVorpal::new, LegendaryVorpal::new, 5),
    DIVINE("Divine", LegendaryDivine.class, LegendaryDivine::new, LegendaryDivine::new, 6),
    GALE("Gale", LegendaryGale.class, LegendaryGale::new, LegendaryGale::new, 7),
    FERVENT("Fervent", LegendaryFervent.class, LegendaryFervent::new, LegendaryFervent::new, 9),
    REQUIEM("Requiem", LegendaryRequiem.class, LegendaryRequiem::new, LegendaryRequiem::new, 10),
    STALWART("Stalwart", LegendaryStalwart.class, LegendaryStalwart::new, LegendaryStalwart::new, 11),
    ENHANCED("Enhanced", LegendaryEnhanced.class, LegendaryEnhanced::new, LegendaryEnhanced::new, 12),
    GRADIENT("Gradient", LegendaryGradient.class, LegendaryGradient::new, LegendaryGradient::new, 13),
    JUGGERNAUT("Juggernaut", LegendaryJuggernaut.class, LegendaryJuggernaut::new, LegendaryJuggernaut::new, 14),
    ARBALEST("Arbalest", LegendaryArbalest.class, LegendaryArbalest::new, LegendaryArbalest::new, 15),
    REVERED("Revered", LegendaryRevered.class, LegendaryRevered::new, LegendaryRevered::new, 0),

    ;


    public static final LegendaryTitles[] VALUES = values();

    public final String name;
    public final Class<?> clazz;
    public final Function<UUID, AbstractLegendaryWeapon> create;
    public final Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon;
    public final int color;

    LegendaryTitles(
            String name, Class<?> clazz,
            Function<UUID, AbstractLegendaryWeapon> create,
            Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon,
            int color
    ) {
        this.name = name;
        this.clazz = clazz;
        this.create = create;
        this.titleWeapon = titleWeapon;
        this.color = color;
    }

}

package com.ebicep.warlords.classes.paladin.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;

public class Avenger extends AbstractPaladin {

    public Avenger() {
        super(
                "Avenger",
                6300,
                305,
                0,
                new AvengersStrike(),
                new ConsecrateAvenger(),
                new LightInfusionAvenger(15.66f),
                new HolyRadianceAvenger(582, 760, 19.57f, 20, 15, 175),
                new AvengersWrath()
        );
    }

}

package com.ebicep.warlords.classes.paladin.specs.avenger;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;

public class Avenger extends AbstractPaladin {

    public Avenger() {
        super("Avenger", 6300, 305, 0,
                new AvengersStrike(),
                new Consecrate(-158.4f, -213.6f, 50, 20, 175, 20),
                new LightInfusion(15.66f),
                new HolyRadiance(19.57f, 20, 15, 175),
                new AvengersWrath());
    }
}

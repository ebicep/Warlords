package com.ebicep.warlords.classes.paladin.specs.protector;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;

public class Protector extends AbstractPaladin {

    public Protector() {
        super(5750, 385, 0,
                new ProtectorsStrike(),
                new Consecrate(-96, -130, 10, 15, 200, 15),
                new LightInfusion(15.66f),
                new HolyRadiance(9.87f, 30, 15, 175),
                new HammerOfLight());
    }

}

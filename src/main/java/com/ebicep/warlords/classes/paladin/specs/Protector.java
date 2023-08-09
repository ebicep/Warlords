package com.ebicep.warlords.classes.paladin.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;

public class Protector extends AbstractPaladin {

    public Protector() {
        super(
                "Protector",
                6000,
                385,
                0,
                new ProtectorsStrike(),
                new Consecrate(96, 130, 10, 15, 200, 15, 4),
                new LightInfusionProtector(15.66f),
                new HolyRadianceProtector(582, 760, 9.87f, 60, 15, 175),
                new HammerOfLight()
        );
    }

}

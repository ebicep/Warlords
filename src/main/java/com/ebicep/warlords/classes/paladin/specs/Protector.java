package com.ebicep.warlords.classes.paladin.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;

public class Protector extends AbstractPaladin {

    public Protector() {
        super(
                "Protector",
                5200,
                385,
                0,
                new ProtectorsStrike(),
                new ConsecrateProtector(),
                new LightInfusionProtector(),
                new HolyRadianceProtector(),
                new HammerOfLight()
        );
    }

}

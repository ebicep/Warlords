package com.ebicep.warlords.classes.paladin.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;

public class Crusader extends AbstractPaladin {

    public Crusader() {
        super(
                "Crusader",
                7400,
                305,
                10,
                new CrusadersStrike(),
                new ConsecrateCrusader(),
                new LightInfusionCrusader(),
                new HolyRadianceCrusader(),
                new InspiringPresence()
        );
    }

}

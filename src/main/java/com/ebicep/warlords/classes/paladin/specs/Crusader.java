package com.ebicep.warlords.classes.paladin.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;

public class Crusader extends AbstractPaladin {

    public Crusader() {
        super(
                "Crusader",
                6850,
                305,
                20,
                new CrusadersStrike(),
                new ConsecrateCrusader(),
                new LightInfusionCrusader(15.66f),
                new HolyRadianceCrusader(582, 760, 19.57f, 20, 15, 175),
                new InspiringPresence()
        );
    }

}

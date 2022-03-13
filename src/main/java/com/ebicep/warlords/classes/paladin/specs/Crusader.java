package com.ebicep.warlords.classes.paladin.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;

public class Crusader extends AbstractPaladin {

    public Crusader() {
        super("Crusader", 6850, 305, 20,
                new CrusadersStrike(),
                new Consecrate(144, 194.4f, 50, 15, 200, 15, 4),
                new LightInfusion(15.66f, -120),
                new HolyRadianceCrusader(582, 760,19.57f, 20, 15, 175),
                new InspiringPresence());
    }
}

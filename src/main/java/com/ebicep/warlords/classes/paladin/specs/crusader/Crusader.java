package com.ebicep.warlords.classes.paladin.specs.crusader;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;

public class Crusader extends AbstractPaladin {

    public Crusader() {
        super("Crusader", 6850, 305, 20,
                new CrusadersStrike(),
                new Consecrate(-144, -194.4f, 50, 15, 200, 15, 4),
                new LightInfusion(15.66f),
                new HolyRadiance(19.57f, 20, 15, 175),
                new InspiringPresence());
    }

}

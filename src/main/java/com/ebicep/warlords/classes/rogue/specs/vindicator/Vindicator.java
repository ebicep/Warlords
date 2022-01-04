package com.ebicep.warlords.classes.rogue.specs.vindicator;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;

public class Vindicator extends AbstractRogue {

    public Vindicator() {
        super("Vindicator", 6000, 305, 10,
                new RighteousStrike(),
                new SoulShackle(),
                new HeartToHeart(),
                new WideGuard(),
                new Vindicate()
        );
    }
}

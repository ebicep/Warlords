package com.ebicep.warlords.classes.rogue.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;

public class Vindicator extends AbstractRogue {

    public Vindicator() {
        super("Vindicator", 6000, 305, 20,
                new RighteousStrike(),
                new SoulShackle(),
                new HeartToHeart(),
                new PrismGuard(),
                new Vindicate()
        );
    }
}

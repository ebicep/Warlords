package com.ebicep.warlords.classes.arcanist.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.arcanist.AbstractArcanist;

public class Sentinel extends AbstractArcanist {

    public Sentinel() {
        super(
                "Sentinel",
                5800,
                305,
                20,
                14,
                0,
                new FortifyingHex(),
                new GuardianBeam(),
                new EnergySeerSentinel(),
                new MysticalBarrier(),
                new Sanctuary()
        );
    }

}

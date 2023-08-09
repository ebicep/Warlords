package com.ebicep.warlords.classes.arcanist.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.arcanist.AbstractArcanist;

public class Sentinel extends AbstractArcanist {

    public Sentinel() {
        super(
                "Sentinel",
                6000,
                305,
                20,
                14,
                15,
                new FortifyingHex(),
                new GuardianBeam(),
                new EnergySeerSentinel(),
                new MysticalBarrier(),
                new Sanctuary()
        );
    }

}

package com.ebicep.warlords.classes.arcanist.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.arcanist.AbstractArcanist;

public class Luminary extends AbstractArcanist {

    public Luminary() {
        super(
                "Luminary",
                5750,
                355,
                20,
                14,
                0,
                new MercifulHex(),
                new RayOfLight(),
                new EnergySeerLuminary(),
                new SanctifiedBeacon(),
                new DivineBlessing()
        );
    }

}

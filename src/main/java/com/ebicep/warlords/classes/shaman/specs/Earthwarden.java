package com.ebicep.warlords.classes.shaman.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;

public class Earthwarden extends AbstractShaman {

    public Earthwarden() {
        super(
                "Earthwarden",
                6300,
                355,
                0,
                new EarthenSpike(),
                new Boulder(),
                new EarthlivingWeapon(),
                new ChainHeal(),
                new HealingTotem()
        );
    }

}
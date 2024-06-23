package com.ebicep.warlords.classes.arcanist.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.classes.arcanist.AbstractArcanist;

public class Conjurer extends AbstractArcanist {

    public Conjurer() {
        super(
                "Conjurer",
                5200,
                305,
                20,
                14,
                0,
                new PoisonousHex(),
                new SoulfireBeam(),
                new EnergySeer(),
                new ContagiousFacade(),
                new AstralPlague()
        );
    }

}

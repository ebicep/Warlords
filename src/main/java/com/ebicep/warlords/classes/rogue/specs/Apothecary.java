package com.ebicep.warlords.classes.rogue.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;

public class Apothecary extends AbstractRogue {

    public Apothecary() {
        super("Apothecary", 5750, 375, 0,
                new ImpalingStrike(),
                new SoothingElixir(),
                new VitalityLiquor(),
                new RemedicChains(),
                new DrainingMiasma()
        );
    }
}

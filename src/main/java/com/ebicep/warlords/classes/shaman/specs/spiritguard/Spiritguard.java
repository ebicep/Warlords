package com.ebicep.warlords.classes.shaman.specs.spiritguard;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;

public class Spiritguard extends AbstractShaman {
    public Spiritguard() {
        super("Spiritguard", 5530, 305, 10,
                new FallenSouls(),
                new SpiritLink(),
                new Soulbinding(),
                new Repentance(),
                new DeathsDebt()
        );
    }

}

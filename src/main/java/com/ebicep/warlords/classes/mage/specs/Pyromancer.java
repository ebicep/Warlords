package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;

public class Pyromancer<Fireball, FlameBurst, TimeWarp, ArcaneShield, Inferno> extends AbstractMage {
    public Pyromancer() {
        super("Pyromancer", 5200, 305, 20, 14, 0,
                new Fireball(),
                new FlameBurst(),
                new TimeWarp(),
                new ArcaneShield(),
                new Inferno());
    }

    @Override
    public String getFormattedData() {
    }
}

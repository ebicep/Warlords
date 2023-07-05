package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.classes.mage.AbstractMage;

public class Pyromancer extends AbstractMage implements WeaponAbilityIcon {

    public Pyromancer() {
        super(
                "Pyromancer",
                5200,
                305,
                20,
                14,
                0,
                new Fireball(),
                new FlameBurst(),
                new TimeWarpPyromancer(),
                new ArcaneShield(),
                new Inferno()
        );
    }

}

package com.ebicep.warlords.classes.arcanist;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;

public abstract class AbstractArcanist extends AbstractPlayerClass {

    public AbstractArcanist(
            String name,
            int maxHealth,
            int maxEnergy,
            int energyPerSec,
            int energyOnHit,
            int damageResistance,
            AbstractAbility weapon,
            AbstractAbility red,
            AbstractAbility purple,
            AbstractAbility blue,
            AbstractAbility orange
    ) {
        super(name, maxHealth, maxEnergy, energyPerSec, energyOnHit, damageResistance, weapon, red, purple, blue, orange);
        this.className = "Arcanist";
        this.classNameShort = "ARC";
    }

}

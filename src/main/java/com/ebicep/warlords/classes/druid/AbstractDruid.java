package com.ebicep.warlords.classes.druid;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;

public abstract class AbstractDruid extends AbstractPlayerClass {

    public AbstractDruid(
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
        this.className = "Druid";
        this.classNameShort = "DRU";
    }

}

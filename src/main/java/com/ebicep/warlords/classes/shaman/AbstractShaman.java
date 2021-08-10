package com.ebicep.warlords.classes.shaman;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;

public abstract class AbstractShaman extends AbstractPlayerClass {

    public AbstractShaman(String name, int maxHealth, int maxEnergy, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
        super(name, maxHealth, maxEnergy, 20, 20, damageResistance, weapon, red, purple, blue, orange);
    }
}
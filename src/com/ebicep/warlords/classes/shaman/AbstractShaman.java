package com.ebicep.warlords.classes.shaman;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.PlayerClass;

public abstract class AbstractShaman extends PlayerClass {

    public AbstractShaman(int maxHealth, int maxEnergy, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
        super(maxHealth, maxEnergy, 20, 20, damageResistance, weapon, red, purple, blue, orange);
    }
}
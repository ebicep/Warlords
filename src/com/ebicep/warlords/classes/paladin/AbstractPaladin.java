package com.ebicep.warlords.classes.paladin;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.PlayerClass;

public abstract class AbstractPaladin extends PlayerClass {

    public AbstractPaladin(int maxHealth, int maxEnergy, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
        super(maxHealth, maxEnergy, 20, 20, damageResistance, weapon, red, purple, blue, orange);
    }
}

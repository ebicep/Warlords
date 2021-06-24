package com.ebicep.warlords.classes.warrior;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.PlayerClass;

public class AbstractWarrior extends PlayerClass {
    public AbstractWarrior(int maxHealth, int maxEnergy, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
        super(maxHealth, maxEnergy, 20, 20, damageResistance, weapon, red, purple, blue, orange);
    }
}

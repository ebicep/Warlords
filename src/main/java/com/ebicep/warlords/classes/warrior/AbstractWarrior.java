package com.ebicep.warlords.classes.warrior;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;

public abstract class AbstractWarrior extends AbstractPlayerClass {

    public AbstractWarrior(
            String name,
            int maxHealth,
            int maxEnergy,
            int damageResistance,
            AbstractAbility weapon,
            AbstractAbility red,
            AbstractAbility purple,
            AbstractAbility blue,
            AbstractAbility orange
    ) {
        super(name, maxHealth, maxEnergy, 20, 20, damageResistance, weapon, red, purple, blue, orange);
        this.className = "Warrior";
        this.classNameShort = "WAR";
    }
}

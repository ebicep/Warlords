package com.ebicep.warlords.classes.rogue;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;

public abstract class AbstractRogue extends AbstractPlayerClass {

    public AbstractRogue(String name, int maxHealth, int maxEnergy, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
        super(name, maxHealth, maxEnergy, 20, 20, damageResistance, weapon, red, purple, blue, orange);
    }
}
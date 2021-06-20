package com.ebicep.warlords.classes.paladin;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import org.bukkit.entity.Player;

public abstract class AbstractPaladin extends AbstractPlayerClass {

    public AbstractPaladin(Player player, int maxHealth, int maxEnergy, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
        super(player, maxHealth, maxEnergy, 20, 20, damageResistance, weapon, red, purple, blue, orange);
    }
}

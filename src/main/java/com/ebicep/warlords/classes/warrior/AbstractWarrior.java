package com.ebicep.warlords.classes.warrior;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import org.bukkit.entity.Player;

public class AbstractWarrior extends AbstractPlayerClass {
    public AbstractWarrior(Player player, int maxHealth, int maxEnergy, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
        super(player, maxHealth, maxEnergy, 20, 20, damageResistance, weapon, red, purple, blue, orange);
    }
}

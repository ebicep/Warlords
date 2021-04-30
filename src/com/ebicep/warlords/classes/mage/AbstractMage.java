package com.ebicep.warlords.classes.mage;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.PlayerClass;
import org.bukkit.entity.Player;

public abstract class AbstractMage extends PlayerClass {

    public AbstractMage(Player player, int maxHealth, int maxEnergy, int energyPerSec, int energyOnHit, int damageResistance, AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange) {
        super(player, maxHealth, maxEnergy, energyPerSec, energyOnHit, damageResistance, weapon, red, purple, blue, orange);
    }
}

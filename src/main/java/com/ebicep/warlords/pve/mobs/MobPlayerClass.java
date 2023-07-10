package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;

import java.util.Arrays;

/**
 * EPS = sum of all abilities energy/cd
 */
public class MobPlayerClass extends AbstractPlayerClass {

    public MobPlayerClass(
            String name,
            int maxHealth,
            int damageResistance,
            AbstractAbility... abilities
    ) {
        this(name, maxHealth, 100, damageResistance, abilities);
    }

    public MobPlayerClass(
            String name,
            int maxHealth,
            int maxEnergy,
            int damageResistance,
            AbstractAbility... abilities
    ) {
        super(name,
                maxHealth,
                maxEnergy,
                (int) Math.round(Arrays.stream(abilities)
                                       .mapToDouble(ability -> ability.getEnergyCost() / ability.getCooldown())
                                       .sum()),
                10,
                damageResistance,
                abilities
        );
    }

}

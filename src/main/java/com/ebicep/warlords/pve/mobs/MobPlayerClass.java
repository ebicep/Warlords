package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;

import java.util.Arrays;

/**
 * <p>Max Energy = Sum of all ability energy costs</p>
 * <p>EPS = sum of all abilities energy/cd</p>
 */
public class MobPlayerClass extends AbstractPlayerClass {

    public MobPlayerClass(
            String name,
            int maxHealth,
            float damageResistance,
            AbstractAbility... abilities
    ) {
        this(name,
                maxHealth,
                (int) Math.round(Arrays.stream(abilities)
                                       .mapToDouble(AbstractAbility::getEnergyCostValue)
                                       .sum()),
                damageResistance,
                abilities
        );
    }

    public MobPlayerClass(
            String name,
            int maxHealth,
            int maxEnergy,
            float damageResistance,
            AbstractAbility... abilities
    ) {
        super(name,
                maxHealth,
                maxEnergy,
                (int) Math.round(Arrays.stream(abilities)
                                       .mapToDouble(ability -> {
                                           if (ability.getCooldownValue() == 0) {
                                               return ability.getEnergyCostValue();
                                           }
                                           return ability.getEnergyCostValue() / ability.getCooldownValue();
                                       })
                                       .sum()),
                0,
                damageResistance,
                abilities
        );
    }

    public void addAbility(AbstractAbility abilityToAdd) {
        abilities.add(abilityToAdd);
        maxEnergy = (int) Math.round(abilities
                .stream()
                .mapToDouble(AbstractAbility::getEnergyCostValue)
                .sum());
        energyPerSec = (int) Math.round(abilities
                .stream()
                .mapToDouble(ability -> {
                    if (ability.getCooldownValue() == 0) {
                        return ability.getEnergyCostValue();
                    }
                    return ability.getEnergyCostValue() / ability.getCooldownValue();
                })
                .sum());
    }

}

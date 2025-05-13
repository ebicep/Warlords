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
                0,
                damageResistance,
                abilities
        );
        this.maxEnergy = (int) Math.round(
                Arrays.stream(abilities)
                      .mapToDouble(AbstractAbility::getEnergyCostValue)
                      .sum()
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
                0,
                0,
                damageResistance,
                abilities
        );
        this.energyPerSec = (int) Math.round(
                Arrays.stream(abilities)
                      .mapToDouble(ability -> {
                          if (ability.getCooldownValue() == 0) {
                              return ability.getEnergyCostValue();
                          }
                          return ability.getEnergyCostValue() / ability.getCooldownValue();
                      })
                      .sum()
        );
    }

    public void addAbility(AbstractAbility abilityToAdd) {
        abilityToAdd.init(abilityToAdd.getBuilder());
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

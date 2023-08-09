package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilities.CrusadersStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class CrusadersStrikeBranch extends AbstractUpgradeBranch<CrusadersStrike> {

    float minDamage;
    float maxDamage;
    float energyCost = ability.getEnergyCost();
    int energyGiven = ability.getEnergyGiven();

    public CrusadersStrikeBranch(AbilityTree abilityTree, CrusadersStrike ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
            ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);
        }
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy cost\n+1 Energy given to allies",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                    ability.setEnergyGiven(energyGiven + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy cost\n+2 Energy given to allies",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                    ability.setEnergyGiven(energyGiven + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy cost\n+3 Energy given to allies",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                    ability.setEnergyGiven(energyGiven + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy cost\n+4 Energy given to allies",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setEnergyGiven(energyGiven + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Crusader’s Slash",
                "Crusader's Strike - Master Upgrade",
                "Double the energy given to allies radius. Additionally, Crusader's Strike hits 2 additional enemies. (excluding energy given)",
                50000,
                () -> {
                    ability.setEnergyRadius(ability.getEnergyRadius() * 2);

                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.ChainLightning;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ChainLightningBranch extends AbstractUpgradeBranch<ChainLightning> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();
    int radius = ability.getRadius();

    public ChainLightningBranch(AbilityTree abilityTree, ChainLightning ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage\n+2 Blocks cast and bounce range",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                    ability.setRadius(radius + 2);
                    ability.setBounceRange(ability.getBounceRange() + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage\n+4 Blocks cast and bounce range",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                    ability.setRadius(radius + 4);
                    ability.setBounceRange(ability.getBounceRange() + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage\n+6 Blocks cast and bounce range",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                    ability.setRadius(radius + 6);
                    ability.setBounceRange(ability.getBounceRange() + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage\n+8 Blocks cast and bounce range",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                    ability.setRadius(radius + 8);
                    ability.setBounceRange(ability.getBounceRange() + 2);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5 Energy cost",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10 Energy cost",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15 Energy cost",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 15);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20 Energy cost\n+1 Chain Bounce",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 20);
                    ability.setAdditionalBounces(ability.getAdditionalBounces() + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Electrifying Chains",
                "Chain Lightning - Master Upgrade",
                "Increase max damage reduction cap by 10%. Additionally, Chain Lightning now deals 10% more damage per bounce instead of less.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setMaxDamageReduction(ability.getMaxDamageReduction() + 10);
                }
        );
    }

}

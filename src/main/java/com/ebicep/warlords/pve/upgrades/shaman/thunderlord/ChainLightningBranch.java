package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.ChainLightning;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ChainLightningBranch extends AbstractUpgradeBranch<ChainLightning> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    int energyCost = ability.getEnergyCost();
    int radius = ability.getRadius();
    int bounceRange = ability.getBounceRange();
    int maxBounces = ability.getMaxBounces();

    public ChainLightningBranch(AbilityTree abilityTree, ChainLightning ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+5% Damage\n+2 Blocks cast and bounce range",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.05f);
                    ability.setMaxDamageHeal(maxDamage * 1.05f);
                    ability.setRadius(radius + 2);
                    ability.setBounceRange(bounceRange + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+10% Damage\n+4 Blocks cast and bounce range",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                    ability.setRadius(radius + 4);
                    ability.setBounceRange(bounceRange + 4);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+15% Damage\n+6 Blocks cast and bounce range",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                    ability.setRadius(radius + 6);
                    ability.setBounceRange(bounceRange + 6);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+20% Damage\n+8 Blocks cast and bounce range",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                    ability.setRadius(radius + 8);
                    ability.setBounceRange(bounceRange + 8);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5 Energy cost\n+1 Chain Bounce",
                5000,
                () -> {
                    ability.setMaxBounces(maxBounces + 1);
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
                "-20 Energy cost\n+2 Chain Bounces",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 20);
                    ability.setMaxBounces(maxBounces + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Electrifying Chains",
                "Chain Lightning - Master Upgrade",
                "Chain Lightning now deals 5% more damage\nper bounce instead of less.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }

    @Override
    public void c1() {
    }

    @Override
    public void c2() {
    }

    @Override
    public void c3() {
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
    }
}

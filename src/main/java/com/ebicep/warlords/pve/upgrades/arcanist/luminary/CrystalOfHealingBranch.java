package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.CrystalOfHealing;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class CrystalOfHealingBranch extends AbstractUpgradeBranch<CrystalOfHealing> {

    float maxHeal = ability.getMaxHeal();
    int lifeSpan = ability.getLifeSpan();

    public CrystalOfHealingBranch(AbilityTree abilityTree, CrystalOfHealing ability) {
        super(abilityTree, ability);


        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+100 Max health",
                5000,
                () -> {
                    ability.setMaxHeal(maxHeal + 100);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+200 Max health",
                10000,
                () -> {
                    ability.setMaxHeal(maxHeal + 200);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+300 Max health",
                15000,
                () -> {
                    ability.setMaxHeal(maxHeal + 300);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+400 Max health",
                20000,
                () -> {
                    ability.setMaxHeal(maxHeal + 400);
                }
        ));


        treeB.add(new Upgrade(
                "Chronos - Tier I",
                "+5s Lifespan",
                10000,
                () -> {
                    ability.setLifeSpan(lifeSpan + 5);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+10s Lifespan",
                15000,
                () -> {
                    ability.setLifeSpan(lifeSpan + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+15s Lifespan",
                20000,
                () -> {
                    ability.setLifeSpan(lifeSpan + 15);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+20s Lifespan",
                25000,
                () -> {
                    ability.setLifeSpan(lifeSpan + 20);
                }
        ));

        masterUpgrade = new Upgrade(
                "NAME",
                "Crystal of Healing - Master Upgrade",
                """
                        Crystal of Healing provide 50 healing per second for allies within 6 blocks.
                        """,
                50000,
                () -> {

                }
        );
    }

}

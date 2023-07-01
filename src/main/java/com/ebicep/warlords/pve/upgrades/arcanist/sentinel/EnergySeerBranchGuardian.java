package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.EnergySeerSentinel;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class EnergySeerBranchGuardian extends AbstractUpgradeBranch<EnergySeerSentinel> {

    float healingMultiplier = ability.getHealingMultiplier();
    int bonusDuration = ability.getBonusDuration();

    public EnergySeerBranchGuardian(AbilityTree abilityTree, EnergySeerSentinel ability) {
        super(abilityTree, ability);


        treeA.add(new Upgrade(
                "Alleviating - Tier I",
                "+25% Healing",
                5000,
                () -> {
                    ability.setHealingMultiplier(healingMultiplier + 0.25f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviating - Tier II",
                "+50% Healing",
                10000,
                () -> {
                    ability.setHealingMultiplier(healingMultiplier + 0.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviating - Tier III",
                "+75% Healing",
                15000,
                () -> {
                    ability.setHealingMultiplier(healingMultiplier + 0.75f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviating - Tier IV",
                "+100% Healing",
                20000,
                () -> {
                    ability.setHealingMultiplier(healingMultiplier + 1f);
                }
        ));

        treeB.add(new Upgrade(
                "Chronos - Tier I",
                "+0.5s Duration",
                5000,
                () -> {
                    ability.setBonusDuration(bonusDuration + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+1s Duration",
                10000,
                () -> {
                    ability.setBonusDuration(bonusDuration + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+1.5s Duration",
                15000,
                () -> {
                    ability.setBonusDuration(bonusDuration + 30);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+2s Duration",
                20000,
                () -> {
                    ability.setBonusDuration(bonusDuration + 40);
                }
        ));

        masterUpgrade = new Upgrade(
                "Electrifying Storm",
                "Healing Rain - Master Upgrade",
                """
                        """,
                50000,
                () -> {

                }
        );
    }

}

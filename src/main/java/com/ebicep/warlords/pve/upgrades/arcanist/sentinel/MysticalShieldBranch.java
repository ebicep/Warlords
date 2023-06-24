package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilties.MysticalBarrier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class MysticalShieldBranch extends AbstractUpgradeBranch<MysticalBarrier> {

    int shieldIncrease = ability.getShieldIncrease();
    int tickDuration = ability.getReactivateTickDuration();

    public MysticalShieldBranch(AbilityTree abilityTree, MysticalBarrier ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+5 Shield Strength Interval",
                5000,
                () -> {
                    ability.setShieldIncrease(shieldIncrease + 5);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+10 Shield Strength Interval",
                10000,
                () -> {
                    ability.setShieldIncrease(shieldIncrease + 10);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+15 Shield Strength Interval",
                15000,
                () -> {
                    ability.setShieldIncrease(shieldIncrease + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+20 Shield Strength Interval",
                20000,
                () -> {
                    ability.setShieldIncrease(shieldIncrease + 20);
                }
        ));

        treeB.add(new Upgrade(
                "Chronos - Tier I",
                "+0.5s Duration",
                5000,
                () -> {
                    ability.setReactivateTickDuration(tickDuration + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+1s Duration",
                10000,
                () -> {
                    ability.setReactivateTickDuration(tickDuration + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+1.5s Duration",
                15000,
                () -> {
                    ability.setReactivateTickDuration(tickDuration + 30);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+2s Duration",
                20000,
                () -> {
                    ability.setReactivateTickDuration(tickDuration + 40);
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

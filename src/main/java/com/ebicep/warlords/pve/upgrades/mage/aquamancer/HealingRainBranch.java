package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilities.HealingRain;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class HealingRainBranch extends AbstractUpgradeBranch<HealingRain> {

    public HealingRainBranch(AbilityTree abilityTree, HealingRain ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability.getHealValues().getRainHealing(), 15f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, 1)
                .addUpgradeDuration(ability, 80f, false, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Electrifying Storm",
                "Healing Rain - Master Upgrade",
                """
                        +4s Additional duration

                        Up to 8 enemies in Healing Rain will be struck with lightning for 224 - 377 (+1% of their max health) damage every 2 seconds.""",
                50000,
                () -> {

                    ability.setTickDuration(ability.getTickDuration() + 80);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Nimbus",
                "Healing Rain - Master Upgrade",
                """
                        Allies within the radius of Healing Rain are instead, granted a personal rain cloud that heals the same as the original, additionally increases EPS by 5 for the duration.
                        The single clouds can follow allies up to 40 blocks away from you.
                        """,
                50000,
                () -> {
                }
        );
    }
}

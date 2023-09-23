package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilities.HealingRain;
import com.ebicep.warlords.pve.upgrades.*;

public class HealingRainBranch extends AbstractUpgradeBranch<HealingRain> {
    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    public HealingRainBranch(AbilityTree abilityTree, HealingRain ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minHealing * value);
                        ability.setMaxDamageHeal(maxHealing * value);
                    }
                }, 15f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
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

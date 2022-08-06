package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilties.HealingRain;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HealingRainBranch extends AbstractUpgradeBranch<HealingRain> {

    int radius = ability.getRadius();
    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    public HealingRainBranch(AbilityTree abilityTree, HealingRain ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+15% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.15f);
                    ability.setMaxDamageHeal(maxHealing * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+30% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+45% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.45f);
                    ability.setMaxDamageHeal(maxHealing * 1.45f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+60% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.6f);
                    ability.setMaxDamageHeal(maxHealing * 1.6f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1 Block rain radius",
                5000,
                () -> {
                    ability.setRadius(radius + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2 Block rain radius",
                10000,
                () -> {
                    ability.setRadius(radius + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3 Block rain radius",
                15000,
                () -> {
                    ability.setRadius(radius + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4 Block rain radius\n+4s Duration",
                20000,
                () -> {
                    ability.setRadius(radius + 4);
                    ability.setDuration(ability.getDuration() + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Electrifying Storm",
                "Healing Rain - Master Upgrade",
                "+4s Addtional duration\n\nUp to 5 enemies in Healing Rain will be struck with\nlightning for 124 - 277 (+1% of their max health)\ndamage every 2 seconds.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setDuration(ability.getDuration() + 4);
                }
        );
    }
}

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
                "Scope - Tier I",
                "+1 Block rain radius",
                5000,
                () -> {
                    ability.setRadius(radius + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier II",
                "+2 Blocks rain radius",
                10000,
                () -> {
                    ability.setRadius(radius + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier III",
                "+3 Blocks rain radius",
                15000,
                () -> {
                    ability.setRadius(radius + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier IV",
                "+4 Blocks rain radius\n+4s Duration",
                20000,
                () -> {
                    ability.setRadius(radius + 4);
                    ability.setDuration(ability.getDuration() + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Electrifying Storm",
                "Healing Rain - Master Upgrade",
                "+4s Additional duration" +
                        "\n\nUp to 8 enemies in Healing Rain will be struck with lightning for 224 - 377 (+1% of their max health) damage every 2 seconds.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setDuration(ability.getDuration() + 4);
                }
        );
    }
}

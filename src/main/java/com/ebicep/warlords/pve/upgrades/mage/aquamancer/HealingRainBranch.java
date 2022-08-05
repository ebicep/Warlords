package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilties.HealingRain;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HealingRainBranch extends AbstractUpgradeBranch<HealingRain> {

    public HealingRainBranch(AbilityTree abilityTree, HealingRain ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Range - Tier I", "+2 Blocks radius", 5000));
        treeA.add(new Upgrade("Range - Tier II", "+4 Blocks radius", 10000));
        treeA.add(new Upgrade("Range - Tier III", "+8 Blocks radius", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+10% Healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+20% Healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+40% Healing", 20000));

        masterUpgrade = new Upgrade(
                "Electrifying Storm",
                "Healing Rain - Master Upgrade",
                "+8s Duration\n\nUp to 5 enemies in Healing Rain will be struck with\nlightning for 124 - 277 (+1% of their max health)\ndamage every 2 seconds.",
                50000
        );
    }

    int radius = ability.getRadius();

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

}

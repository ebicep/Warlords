package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilties.WaterBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WaterBoltBranch extends AbstractUpgradeBranch<WaterBolt> {

    public WaterBoltBranch(AbilityTree abilityTree, WaterBolt ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Energy - Tier I", "-10 Energy cost", 5000));
        treeA.add(new Upgrade("Energy - Tier II", "-20 Energy cost", 10000));
        treeA.add(new Upgrade("Energy - Tier III", "-30 Energy cost", 20000));

        treeC.add(new Upgrade("Damage/Healing - Tier I", "+10% Damage and Healing", 5000));
        treeC.add(new Upgrade("Damage/Healing - Tier II", "+20% Damage and Healing", 10000));
        treeC.add(new Upgrade("Damage/Healing - Tier III", "+40% Damage and Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+100% Projectile speed\n\nWater Bolt increases the damage dealt\nof all allies it hits by 15% for 10 seconds.",
                50000
        );
    }

    float energyCost = ability.getEnergyCost();

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float minDamage = ability.getMinDamage();
    float maxDamage = ability.getMaxDamage();

}

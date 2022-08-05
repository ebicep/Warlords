package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilties.WaterBreath;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WaterBreathBranch extends AbstractUpgradeBranch<WaterBreath> {

    public WaterBreathBranch(AbilityTree abilityTree, WaterBreath ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown Reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown Reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown Reduction", 20000));

        treeB.add(new Upgrade("Utility - Tier I", "+10% Knockback", 5000));
        treeB.add(new Upgrade("Utility - Tier II", "+25% Knockback", 10000));
        treeB.add(new Upgrade("Utility - Tier III", "+50% Knockback", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+15% Healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+30% Healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+60% Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+30% Cone Range\n\nAll allies hit by Water Breath are healed of their\n1% max health per second for 5 seconds.",
                50000
        );
    }

    float cooldown = ability.getCooldown();

    double velocity = ability.getVelocity();

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

}

package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilties.FlameBurst;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FlameburstBranch extends AbstractUpgradeBranch<FlameBurst> {

    float cooldown = ability.getCooldown();
    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();
    float critMultiplier = ability.getCritMultiplier();

    public FlameburstBranch(AbilityTree abilityTree, FlameBurst ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        treeB.add(new Upgrade("Range - Tier I", "+1 Block splash radius", 5000));
        treeB.add(new Upgrade("Range - Tier II", "+2 Blocks splash radius", 10000));
        treeB.add(new Upgrade("Range - Tier III", "+3 Blocks splash radius", 20000));

        treeC.add(new Upgrade("Damage - Tier I", "+15% Damage", 5000));
        treeC.add(new Upgrade("Damage - Tier II", "+30% Damage", 10000));
        treeC.add(new Upgrade("Damage - Tier III", "+60% Damage", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "PLACEHOLDER",
                50000,
                () -> {
                    ability.setAcceleration(1.005);
                    ability.setProjectileSpeed(ability.getProjectileSpeed() * 0.2);
                    ability.setEnergyCost(energyCost + 140);
                    ability.setMinDamageHeal(minDamage * 2);
                    ability.setMaxDamageHeal(maxDamage * 2);
                    ability.setCooldown(cooldown * 2);
                    ability.setHitbox(ability.getHitbox() + 5);
                    ability.setPveUpgrade(true);
                }
        );
    }
}

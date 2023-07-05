package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilities.WaterBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WaterBoltBranch extends AbstractUpgradeBranch<WaterBolt> {

    float energyCost = ability.getEnergyCost();
    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float minDamage = ability.getMinDamage();
    float maxDamage = ability.getMaxDamage();
    float hitbox = ability.getHitbox();

    public WaterBoltBranch(AbilityTree abilityTree, WaterBolt ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+15% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.15f);
                    ability.setMaxDamageHeal(maxHealing * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+30% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+45% Healing\n+15% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.45f);
                    ability.setMaxDamageHeal(maxHealing * 1.45f);
                    ability.setMinDamage(minDamage * 1.15f);
                    ability.setMaxDamage(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+60% Healing\n+30% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.6f);
                    ability.setMaxDamageHeal(maxHealing * 1.6f);
                    ability.setMinDamage(minDamage * 1.3f);
                    ability.setMaxDamage(maxDamage * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5 Energy cost\n+0.5 Blocks hit radius",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 5f);
                    ability.setHitbox(hitbox + 0.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10 Energy cost\n+1 Blocks hit radius",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setHitbox(hitbox + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15 Energy cost\n+1.5 Blocks hit radius",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 15f);
                    ability.setHitbox(hitbox + 1.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20 Energy cost\n+2 Blocks hit radius",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 20);
                    ability.setHitbox(hitbox + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Aquatic Assault",
                "Water Bolt - Master Upgrade",
                "+100% Projectile speed\n\nWater Bolt increases the damage dealt of all allies it hits by 10% for 10 seconds.",
                50000,
                () -> {

                    ability.setProjectileSpeed(ability.getProjectileSpeed() * 2);
                }
        );
    }
}

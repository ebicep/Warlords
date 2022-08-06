package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilties.WaterBolt;
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
                "+7.5% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.075f);
                    ability.setMaxDamageHeal(maxHealing * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.15f);
                    ability.setMaxDamageHeal(maxHealing * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Healing\n+7.5% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.225f);
                    ability.setMaxDamageHeal(maxHealing * 1.225f);
                    ability.setMinDamage(minDamage * 1.075f);
                    ability.setMaxDamage(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Healing\n+15% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
                    ability.setMinDamage(minDamage * 1.15f);
                    ability.setMaxDamage(maxDamage * 1.15f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy cost\n+0.5 Blocks hit radius",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                    ability.setHitbox(hitbox + 0.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy cost\n+1 Blocks hit radius",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                    ability.setHitbox(hitbox + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy cost\n+1.5 Blocks hit radius",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                    ability.setHitbox(hitbox + 1.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy cost\n+2 Blocks hit radius",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setHitbox(hitbox + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Aquatic Assault",
                "Water Bolt - Master Upgrade",
                "+100% Projectile speed\n\nWater Bolt increases the damage dealt\nof all allies it hits by 15% for 10 seconds.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setProjectileSpeed(ability.getProjectileSpeed() * 2);
                }
        );
    }
}

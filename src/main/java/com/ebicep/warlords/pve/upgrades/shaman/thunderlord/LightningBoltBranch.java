package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightningBoltBranch extends AbstractUpgradeBranch<LightningBolt> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    double projectileSpeed = ability.getProjectileSpeed();
    float energyCost = ability.getEnergyCost();
    double hitbox = ability.getHitbox();

    public LightningBoltBranch(AbilityTree abilityTree, LightningBolt ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage\n+20% Projectile speed",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                    ability.setProjectileSpeed(projectileSpeed * 1.2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage\n+40% Projectile speed",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                    ability.setProjectileSpeed(projectileSpeed * 1.4);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage\n+60% Projectile speed",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                    ability.setProjectileSpeed(projectileSpeed * 1.6);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage\n+80% Projectile speed",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                    ability.setProjectileSpeed(projectileSpeed * 1.8);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy cost\n+0.25 Block hit radius",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                    ability.setHitbox(hitbox + 0.25);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy cost\n+0.5 Block hit radius",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                    ability.setHitbox(hitbox + 0.5);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy cost\n+0.75 Block hit radius",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                    ability.setHitbox(hitbox + 0.75);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy cost\n+1 Block hit radius",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setHitbox(hitbox + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Lightning Volley",
                "Lightning Bolt - Master Upgrade",
                "Lightning Bolt shoots two additional projectiles.",
                50000,
                () -> {
                    ability.setShotsFiredAtATime(3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Electric Bolt",
                "Lightning Bolt - Master Upgrade",
                """
                        Each additional enemy hit takes 20% more damage. Max of 5 additional enemies will receive this increase in damage, further enemies will be hit the same as the first.
                        """,
                50000,
                () -> {
                }
        );
    }


}

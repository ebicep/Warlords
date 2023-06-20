package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilties.Fireball;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FireballBranch extends AbstractUpgradeBranch<Fireball> {

    double projectileSpeed = ability.getProjectileSpeed();
    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float hitbox = ability.getHitbox();
    float energyCost = ability.getEnergyCost();

    public FireballBranch(AbilityTree abilityTree, Fireball ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage\n+50% Projectile speed",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                    ability.setProjectileSpeed(projectileSpeed * 1.5f);
                }
        ));

        treeB.add(new Upgrade(
                "Scope - Tier I",
                "+0.5 Blocks hit radius\n-5 Energy cost",
                5000,
                () -> {
                    ability.setHitbox(hitbox + 0.5f);
                    ability.setEnergyCost(energyCost - 5);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier II",
                "+1 Blocks hit radius\n-10 Energy cost",
                10000,
                () -> {
                    ability.setHitbox(hitbox + 1f);
                    ability.setEnergyCost(energyCost - 10);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier III",
                "+1.5 Blocks hit radius\n-15 Energy cost",
                15000,
                () -> {
                    ability.setHitbox(hitbox + 1.5f);
                    ability.setEnergyCost(energyCost - 15);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier IV",
                "+2 Block hit radius\n-20 Energy cost",
                20000,
                () -> {
                    ability.setHitbox(hitbox + 2);
                    ability.setEnergyCost(energyCost - 20);
                }
        ));

        masterUpgrade = new Upgrade(
                "Fiery Fusillade",
                "Fireball - Master Upgrade",
                """
                        Direct-hits apply the BURN status for 5 seconds.

                        BURN: Enemies take 20% more damage from all sources and burn for 0.5% of their max health every second.""",
                50000,
                () -> {
                    ability.setPveMasterUpgrade(true);
                }
        );
    }
}

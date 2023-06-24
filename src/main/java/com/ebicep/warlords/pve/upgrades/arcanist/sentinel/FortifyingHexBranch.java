package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilties.FortifyingHex;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FortifyingHexBranch extends AbstractUpgradeBranch<FortifyingHex> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();
    float hitbox = ability.getPlayerHitbox();
    double projectileSpeed = ability.getProjectileSpeed();

    public FortifyingHexBranch(AbilityTree abilityTree, FortifyingHex ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.05f);
                    ability.setMaxDamageHeal(maxDamage * 1.05f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+10% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+15% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+20% Damage\n+50% Projectile speed",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                    ability.setProjectileSpeed(projectileSpeed * 1.5f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy cost\n+0.25 Block hit radius",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                    ability.setPlayerHitbox(hitbox + 0.25f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy cost\n+0.5 Block hit radius",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                    ability.setPlayerHitbox(hitbox + 0.5f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy cost\n+0.75 Block hit radius",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                    ability.setPlayerHitbox(hitbox + 0.75f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy cost\n+1 Block hit radius",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setPlayerHitbox(hitbox + 1f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Electrifying Storm",
                "Healing Rain - Master Upgrade",
                """
                        """,
                50000,
                () -> {

                }
        );
    }

}

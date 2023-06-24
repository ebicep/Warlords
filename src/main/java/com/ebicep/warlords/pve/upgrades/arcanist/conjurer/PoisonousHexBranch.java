package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilties.PoisonousHex;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class PoisonousHexBranch extends AbstractUpgradeBranch<PoisonousHex> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();
    double projectileSpeed = ability.getProjectileSpeed();

    public PoisonousHexBranch(AbilityTree abilityTree, PoisonousHex ability) {
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
                    ability.setProjectileSpeed(projectileSpeed * 1.5);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5 Energy cost",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10 Energy cost",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 10f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15 Energy cost",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 15f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20 Energy cost",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 20f);
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

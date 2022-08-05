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
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "",
                5000,
                () -> {

                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "",
                10000,
                () -> {

                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "",
                15000,
                () -> {

                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "",
                20000,
                () -> {

                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "",
                5000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "",
                10000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "",
                15000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "",
                20000,
                () -> {

                }
        ));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "PLACEHOLDER",
                50000,
                () -> {
                    ability.setProjectileWidth(0.72D);
                    ability.setAcceleration(1.005);
                    ability.setProjectileSpeed(ability.getProjectileSpeed() * 0.2);
                    ability.setEnergyCost(energyCost + 90);
                    ability.setMinDamageHeal(minDamage * 2);
                    ability.setMaxDamageHeal(maxDamage * 2);
                    ability.setCooldown(cooldown * 2);
                    ability.setHitbox(ability.getHitbox() + 5);
                    ability.setPveUpgrade(true);
                }
        );
    }
}

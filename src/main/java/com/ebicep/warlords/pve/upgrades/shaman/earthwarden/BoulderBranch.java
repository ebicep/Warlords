package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.Boulder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BoulderBranch extends AbstractUpgradeBranch<Boulder> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    double velocity = ability.getVelocity();
    double hitbox = ability.getHitbox();

    public BoulderBranch(AbilityTree abilityTree, Boulder ability) {
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
                "",
                "",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setCooldown(ability.getCooldown() * 3);
                    ability.setEnergyCost(200);
                    ability.setMinDamageHeal(minDamage * 8);
                    ability.setMinDamageHeal(maxDamage * 8);
                    ability.setHitbox(hitbox + 3);
                }
        );
    }
}

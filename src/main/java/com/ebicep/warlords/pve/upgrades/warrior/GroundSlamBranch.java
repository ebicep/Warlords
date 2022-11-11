package com.ebicep.warlords.pve.upgrades.warrior;

import com.ebicep.warlords.abilties.GroundSlam;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class GroundSlamBranch extends AbstractUpgradeBranch<GroundSlam> {

    int slamSize = ability.getSlamSize();
    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public GroundSlamBranch(AbilityTree abilityTree, GroundSlam ability) {
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
                "+30% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1 Block hit radius",
                5000,
                () -> {
                    ability.setSlamSize(slamSize + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2 Block hit radius",
                10000,
                () -> {
                    ability.setSlamSize(slamSize + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3 Blocks hit radius",
                15000,
                () -> {
                    ability.setSlamSize(slamSize + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4 Blocks hit radius",
                20000,
                () -> {
                    ability.setSlamSize(slamSize + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Earthen Tremor",
                "Ground Slam - Master Upgrade",
                "Casting Ground Slam will leap you in the air for a short duration. Upon landing, activate a second Ground Slam for 150% of the original damage.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}

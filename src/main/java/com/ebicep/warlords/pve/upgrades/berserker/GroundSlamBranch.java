package com.ebicep.warlords.pve.upgrades.berserker;

import com.ebicep.warlords.abilties.GroundSlam;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class GroundSlamBranch extends AbstractUpgradeBranch<GroundSlam> {

    int slamSize = ability.getSlamSize();
    float cooldown = ability.getCooldown();

    public GroundSlamBranch(AbilityTree abilityTree, GroundSlam ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "-2.5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.975f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "-5% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "-7.5% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.925f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "-10% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
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
                "Casting Ground Slam will leap you in the air\nfor a short duration. Upon landing, activate a\nsecond Ground Slam for 30% of the original\ndamage.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}

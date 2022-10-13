package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilties.SoulSwitch;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoulSwitchBranch extends AbstractUpgradeBranch<SoulSwitch> {

    int radius = ability.getRadius();
    float cooldown = ability.getCooldown();

    public SoulSwitchBranch(AbilityTree abilityTree, SoulSwitch ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "-10% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "-20% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "-30% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.7f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "-40% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.6f);
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
                "",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}

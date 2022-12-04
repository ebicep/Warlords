package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilties.BloodLust;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BloodlustBranch extends AbstractUpgradeBranch<BloodLust> {

    float conversion = ability.getMaxConversionAmount();
    float cooldown = ability.getCooldown();

    public BloodlustBranch(AbilityTree abilityTree, BloodLust ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+75 Max converted healing",
                5000,
                () -> {
                    ability.setMaxConversionAmount(conversion + 75);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+150 Max converted healing",
                10000,
                () -> {
                    ability.setMaxConversionAmount(conversion + 150);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+225 Max converted healing",
                15000,
                () -> {
                    ability.setMaxConversionAmount(conversion + 225);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+300 Max converted healing",
                20000,
                () -> {
                    ability.setMaxConversionAmount(conversion + 300);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "-3.75% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.9725f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier II",
                "-7.5% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.925f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "-11.25% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.8875f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "-15% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Sanguineous",
                "Blood Lust - Master Upgrade",
                "+5s Duration\n\nWhile Blood Lust is active, increase all damage against bleeding or wounded targets by 40%",
                50000,
                () -> {
                    ability.setDuration(ability.getDuration() + 5);
                    ability.setPveUpgrade(true);
                }
        );
    }
}

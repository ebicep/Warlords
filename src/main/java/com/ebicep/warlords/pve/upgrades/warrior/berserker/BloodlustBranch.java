package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilties.BloodLust;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BloodlustBranch extends AbstractUpgradeBranch<BloodLust> {

    float cooldown = ability.getCooldown();

    public BloodlustBranch(AbilityTree abilityTree, BloodLust ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+10% Max converted health",
                5000,
                () -> {
                    ability.setMaxConversionPercent(ability.getMaxConversionPercent() + 10);
                    ability.updateCustomStats(abilityTree.getPlayer().getSpec());
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+20% Max converted health",
                10000,
                () -> {
                    ability.setMaxConversionPercent(ability.getMaxConversionPercent() + 20);
                    ability.updateCustomStats(abilityTree.getPlayer().getSpec());
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+30% Max converted health",
                15000,
                () -> {
                    ability.setMaxConversionPercent(ability.getMaxConversionPercent() + 30);
                    ability.updateCustomStats(abilityTree.getPlayer().getSpec());
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+40% Max converted health",
                20000,
                () -> {
                    ability.setMaxConversionPercent(ability.getMaxConversionPercent() + 40);
                    ability.updateCustomStats(abilityTree.getPlayer().getSpec());
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

package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilities.BloodLust;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BloodlustBranch extends AbstractUpgradeBranch<BloodLust> {

    float cooldown = ability.getCooldown();
    float healReductionPercent = ability.getHealReductionPercent();

    public BloodlustBranch(AbilityTree abilityTree, BloodLust ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "-3.75% Multi hit health reduction",
                5000,
                () -> {
                    ability.setHealReductionPercent(healReductionPercent + 3.75f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "-7.5% Multi hit health reduction",
                10000,
                () -> {
                    ability.setHealReductionPercent(healReductionPercent + 7.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "-11.25% Multi hit health reduction",
                15000,
                () -> {
                    ability.setHealReductionPercent(healReductionPercent + 11.25f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "-15% Multi hit health reduction",
                20000,
                () -> {
                    ability.setHealReductionPercent(healReductionPercent + 15);
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
                    ability.setTickDuration(ability.getTickDuration() + 100);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Blood Thirsty",
                "Blood Lust - Master Upgrade",
                """
                        +5s Duration
                                                
                        While Blood Lust is active, each kill will reduce the cooldown of Berserk by 0.5s. Max reduction of 5s.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 100);

                }
        );
    }
}

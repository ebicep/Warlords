package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilties.BloodLust;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BloodlustBranch extends AbstractUpgradeBranch<BloodLust> {

    int conversion = ability.getDamageConvertPercent();
    float cooldown = ability.getCooldown();

    public BloodlustBranch(AbilityTree abilityTree, BloodLust ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+3% Damage conversion to healing",
                5000,
                () -> {
                    ability.setDamageConvertPercent(conversion + 3);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+6% Damage conversion to healing",
                10000,
                () -> {
                    ability.setDamageConvertPercent(conversion + 6);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+9% Damage conversion to healing",
                15000,
                () -> {
                    ability.setDamageConvertPercent(conversion + 9);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+12% Damage conversion to healing",
                20000,
                () -> {
                    ability.setDamageConvertPercent(conversion + 12);
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
                "+3s Duration\n\nWhile Blood Lust is active, increase all damage against bleeding or wounded targets by 20%",
                50000,
                () -> {
                    ability.setDuration(ability.getDuration() + 3);
                    ability.setPveUpgrade(true);
                }
        );
    }
}

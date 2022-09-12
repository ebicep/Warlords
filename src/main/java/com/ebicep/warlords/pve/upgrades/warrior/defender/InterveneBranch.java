package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilties.Intervene;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class InterveneBranch extends AbstractUpgradeBranch<Intervene> {

    int castRadius = ability.getRadius();
    int breakRadius = ability.getBreakRadius();
    float cooldown = ability.getCooldown();
    float maxDamagePrevented = ability.getMaxDamagePrevented();

    public InterveneBranch(AbilityTree abilityTree, Intervene ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "-3% Cooldown reduction\n+150 Max damage prevented",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.97f);
                    ability.setMaxDamagePrevented(maxDamagePrevented + 150);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "-6% Cooldown reduction\n+300 Max damage prevented",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.94f);
                    ability.setMaxDamagePrevented(maxDamagePrevented + 300);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "-9% Cooldown reduction\n+450 Max damage prevented",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.91f);
                    ability.setMaxDamagePrevented(maxDamagePrevented + 450);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "-12% Cooldown reduction\n+600 Max damage prevented",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.88f);
                    ability.setMaxDamagePrevented(maxDamagePrevented + 600);
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
                "Remove the cast and break range limit on Intervene. Additionally, ",
                50000,
                () -> {
                    ability.setRadius(200);
                    ability.setBreakRadius(200);
                }
        );
    }
}

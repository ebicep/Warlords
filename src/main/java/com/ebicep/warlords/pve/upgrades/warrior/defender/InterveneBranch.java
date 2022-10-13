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
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);

                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+250 Max damage prevented",
                5000,
                () -> {
                    ability.setMaxDamagePrevented(maxDamagePrevented + 250);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+500 Max damage prevented",
                10000,
                () -> {
                    ability.setMaxDamagePrevented(maxDamagePrevented + 500);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+750 Max damage prevented",
                15000,
                () -> {
                    ability.setMaxDamagePrevented(maxDamagePrevented + 750);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+1000 Max damage prevented",
                20000,
                () -> {
                    ability.setMaxDamagePrevented(maxDamagePrevented + 1000);
                }
        ));

        masterUpgrade = new Upgrade(
                "Intersection",
                "Intervene - Master Upgrade",
                "Remove the cast and break range limit on Intervene.",
                50000,
                () -> {
                    ability.setRadius(300);
                    ability.setBreakRadius(300);
                }
        );
    }
}

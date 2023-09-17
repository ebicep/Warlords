package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.OrderOfEviscerate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class OrderOfEviscerateBranch extends AbstractUpgradeBranch<OrderOfEviscerate> {

    float cooldown = ability.getCooldown();
    int duration = ability.getTickDuration();

    public OrderOfEviscerateBranch(AbilityTree abilityTree, OrderOfEviscerate ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.setInPve(true);
        }

        treeA.add(new Upgrade(
                "Chronos - Tier I",
                "+2s Duration",
                5000,
                () -> {
                    ability.setTickDuration(duration + 40);
                }
        ));
        treeA.add(new Upgrade(
                "Chronos - Tier II",
                "+4s Duration",
                10000,
                () -> {
                    ability.setTickDuration(duration + 80);
                }
        ));
        treeA.add(new Upgrade(
                "Chronos - Tier III",
                "+6s Duration",
                15000,
                () -> {
                    ability.setTickDuration(duration + 120);
                }
        ));
        treeA.add(new Upgrade(
                "Chronos - Tier IV",
                "+8s Duration",
                20000,
                () -> {
                    ability.setTickDuration(duration + 160);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Killing Order",
                "Order of Eviscerate - Master Upgrade",
                "Kills while Order of Eviscerate is active reduce the cooldown by an additional 4 seconds. Additionally, attacks from behind deal 70% more damage.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Cloaked Engagement",
                "Order of Eviscerate - Master Upgrade",
                """
                        Killing your mark will now increase your damage by 45% for 5s, max 2 stacks.
                        """,
                50000,
                () -> {

                }
        );
    }
}

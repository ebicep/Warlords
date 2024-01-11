package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.OrderOfEviscerate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class OrderOfEviscerateBranch extends AbstractUpgradeBranch<OrderOfEviscerate> {


    public OrderOfEviscerateBranch(AbilityTree abilityTree, OrderOfEviscerate ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 40f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);


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

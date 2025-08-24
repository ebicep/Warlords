package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.OrderOfEviscerate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class OrderOfEviscerateBranch extends AbstractUpgradeBranch<OrderOfEviscerate> {

    @Override
    public void runOnce() {
        ability.setVulnerableDamageBonus(ability.getVulnerableDamageBonus() + 20);
    }

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
                """
                        +4s Duration
                        
                        Kills while Order of Eviscerate is active reduce the cooldown by an additional 4 seconds. Additionally, attacks from behind deal 70% more damage.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 80);
                    ability.setVulnerableDamageBonus(ability.getVulnerableDamageBonus() + 70);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Cloaked Engagement",
                "Order of Eviscerate - Master Upgrade",
                """
                        Killing your mark will now increase your damage by 40% for 8s, max 2 stacks.
                        """,
                50000,
                () -> {

                }
        );
    }
}

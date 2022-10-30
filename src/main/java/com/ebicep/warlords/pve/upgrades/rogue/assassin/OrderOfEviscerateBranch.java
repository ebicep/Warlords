package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilties.OrderOfEviscerate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class OrderOfEviscerateBranch extends AbstractUpgradeBranch<OrderOfEviscerate> {

    float cooldown = ability.getCooldown();
    int duration = ability.getDuration();

    public OrderOfEviscerateBranch(AbilityTree abilityTree, OrderOfEviscerate ability) {
        super(abilityTree, ability);
        ability.setPveUpgrade(true);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+2s Duration",
                5000,
                () -> {
                    ability.setDuration(duration + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+4s Duration",
                10000,
                () -> {
                    ability.setDuration(duration + 4);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+6s Duration",
                15000,
                () -> {
                    ability.setDuration(duration + 6);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+8s Duration",
                20000,
                () -> {
                    ability.setDuration(duration + 8);
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
                "Triple backstab damage, kills while Order of Eviscerate is active reduce the cooldown by an additional 3 seconds.",
                50000,
                () -> {
                    ability.setMasterUpgrade(true);
                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilities.HeartToHeart;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HeartToHeartBranch extends AbstractUpgradeBranch<HeartToHeart> {

    int radius = ability.getRadius();
    int verticalRadius = ability.getVerticalRadius();
    float cooldown = ability.getCooldown();

    public HeartToHeartBranch(AbilityTree abilityTree, HeartToHeart ability) {
        super(abilityTree, ability);
        
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+2 Blocks hit radius",
                5000,
                () -> {
                    ability.setRadius(radius + 2);
                    ability.setVerticalRadius(verticalRadius + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+4 Blocks hit radius",
                10000,
                () -> {
                    ability.setRadius(radius + 4);
                    ability.setVerticalRadius(verticalRadius + 4);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+6 Blocks hit radius",
                15000,
                () -> {
                    ability.setRadius(radius + 6);
                    ability.setVerticalRadius(verticalRadius + 6);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+8 Blocks hit radius",
                20000,
                () -> {
                    ability.setRadius(radius + 8);
                    ability.setVerticalRadius(verticalRadius + 8);
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
                "Impair - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Heart of Hearts",
                "Heart To Heart - Master Upgrade",
                "Heart To Heart now deals 1635 - 2096 damage to all enemies you pass through. Enemies hit are stunned for 1 second.",
                50000,
                () -> {

                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.PrismGuard;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class PrismGuardBranch extends AbstractUpgradeBranch<PrismGuard> {

    int duration;
    int bubbleHealing = ability.getBubbleHealing();
    float bubbleMissingHealing = ability.getBubbleMissingHealing();

    public PrismGuardBranch(AbilityTree abilityTree, PrismGuard ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.setTickDuration(120);
            ability.setProjectileDamageReduction(75);
        }
        duration = ability.getTickDuration();

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+100 Base healing\n+1% Missing health healing.",
                5000,
                () -> {
                    ability.setBubbleHealing(bubbleHealing + 100);
                    ability.setBubbleMissingHealing(bubbleMissingHealing + 1);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+200 Base healing\n+2% Missing health healing.",
                10000,
                () -> {
                    ability.setBubbleHealing(bubbleHealing + 200);
                    ability.setBubbleMissingHealing(bubbleMissingHealing + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+300 Base healing\n+3% Missing health healing.",
                15000,
                () -> {
                    ability.setBubbleHealing(bubbleHealing + 300);
                    ability.setBubbleMissingHealing(bubbleMissingHealing + 3);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+400 Base healing\n+4% Missing health healing.",
                20000,
                () -> {
                    ability.setBubbleHealing(bubbleHealing + 400);
                    ability.setBubbleMissingHealing(bubbleMissingHealing + 4);
                }
        ));

        treeB.add(new Upgrade(
                "Chronos - Tier I",
                "+1s Duration",
                5000,
                () -> {
                    ability.setTickDuration(duration + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+2s Duration",
                10000,
                () -> {
                    ability.setTickDuration(duration + 40);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+3s Duration",
                15000,
                () -> {
                    ability.setTickDuration(duration + 60);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+4s Duration",
                20000,
                () -> {
                    ability.setTickDuration(duration + 80);
                }
        ));

        masterUpgrade = new Upgrade(
                "Deflective Tesseract",
                "Prism Guard - Master Upgrade",
                "Gain 10% damage reduction and enemies in a 15 block radius will target you while Prism Guard is active. Additionally, increase your passive damage reduction by 25%",
                50000,
                () -> {
                    abilityTree.getWarlordsPlayer().setDamageResistance(abilityTree.getWarlordsPlayer().getSpec().getDamageResistance() + 25);
                    ability.setPveMasterUpgrade(true);
                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.PrismGuard;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class PrismGuardBranch extends AbstractUpgradeBranch<PrismGuard> {

    int projectileDamageReduction = ability.getProjectileDamageReduction();
    int bubbleHealing = ability.getBubbleHealing();
    float bubbleMissingHealing = ability.getBubbleMissingHealing();

    public PrismGuardBranch(AbilityTree abilityTree, PrismGuard ability) {
        super(abilityTree, ability);
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
                "Spark - Tier I",
                "+10% Projectile Damage reduction",
                5000,
                () -> {
                    ability.setProjectileDamageReduction(projectileDamageReduction + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+15% Projectile Damage reduction",
                10000,
                () -> {
                    ability.setProjectileDamageReduction(projectileDamageReduction + 15);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+20% Projectile Damage reduction",
                15000,
                () -> {
                    ability.setProjectileDamageReduction(projectileDamageReduction + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+25% Projectile Damage reduction",
                20000,
                () -> {
                    ability.setProjectileDamageReduction(projectileDamageReduction + 25);
                }
        ));

        masterUpgrade = new Upgrade(
                "Deflective Tesseract",
                "Prism Guard - Master Upgrade",
                "Enemies in a 15 block radius will target you while Prism Guard is active. Additionally, increase your passive damage reduction by 25%",
                50000,
                () -> {
                    abilityTree.getPlayer().getSpec().setDamageResistance(abilityTree.getPlayer().getSpec().getDamageResistance() + 25);
                    ability.setDuration(ability.getDuration() * 2);
                    ability.setPveUpgrade(true);
                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.mage;

import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ArcaneShieldBranch extends AbstractUpgradeBranch<ArcaneShield> {

    float cooldown = ability.getCooldown();
    int shieldPercentage = ability.getShieldPercentage();

    public ArcaneShieldBranch(AbilityTree abilityTree, ArcaneShield ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+5% Shield health",
                5000,
                () -> {
                    ability.setShieldPercentage(shieldPercentage + 5);
                    ability.updateShieldHealth(abilityTree.getPlayer().getSpec());
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+10% Shield health",
                10000,
                () -> {
                    ability.setShieldPercentage(shieldPercentage + 10);
                    ability.updateShieldHealth(abilityTree.getPlayer().getSpec());
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+15% Shield health",
                15000,
                () -> {
                    ability.setShieldPercentage(shieldPercentage + 15);
                    ability.updateShieldHealth(abilityTree.getPlayer().getSpec());
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+20% Shield health",
                20000,
                () -> {
                    ability.setShieldPercentage(shieldPercentage + 20);
                    ability.updateShieldHealth(abilityTree.getPlayer().getSpec());
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Arcane Aegis",
                "Arcane Shield - Master Upgrade",
                "When arcane shield ends or breaks, unleash\na shockwave that stuns enemies for 6 seconds.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}

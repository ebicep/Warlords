package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilties.UndyingArmy;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class UndyingArmyBranch extends AbstractUpgradeBranch<UndyingArmy> {

    float flatHealing = ability.getFlatHealing();
    float cooldown = ability.getCooldown();

    public UndyingArmyBranch(AbilityTree abilityTree, UndyingArmy ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+25 Flat healing",
                5000,
                () -> {
                    ability.setFlatHealing(flatHealing + 25);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+50 Flat healing",
                10000,
                () -> {
                    ability.setFlatHealing(flatHealing + 50);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+75 Flat healing",
                15000,
                () -> {
                    ability.setFlatHealing(flatHealing + 75);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+100 Flat healing",
                20000,
                () -> {
                    ability.setFlatHealing(flatHealing + 100);
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
                "Relentless Army",
                "Undying Army - Master Upgrade",
                "While dead, gain 40% speed and deal 68-102 + 0.5% of the " +
                        "enemy’s maximum health to all enemies within a 6 block radius. " +
                        "Each enemy hit this way can also proc Orbs of Life.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}

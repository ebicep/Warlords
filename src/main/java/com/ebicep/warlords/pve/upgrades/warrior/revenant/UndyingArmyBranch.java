package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.UndyingArmy;
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
                "Double the range of Undying Army. Additionally, while dead, take half the damage you would normally take" +
                        " and gain 40% speed and deal 458-612 + 2% of the " +
                        "enemy’s maximum health to all enemies within a 6 block radius. " +
                        "Each enemy hit this way can also proc Orbs of Life.",
                50000,
                () -> {
                    ability.setRadius(ability.getRadius() * 2);
                    ability.setMaxHealthDamage((int) (ability.getMaxHealthDamage() * 0.5f));

                }
        );
        masterUpgrade2 = new Upgrade(
                "Vengeful Army",
                "Undying Army - Master Upgrade",
                """
                        Upon casting, enemies within the radius are marked for the duration of Undying Army.
                        Marked enemies build up stacks of Vengeance that accrue every second, each stack equals 100hp.
                        After 10s marked enemies pop taking damage based on stacks accrued as well as 10% of their max hp.
                        If Orbs of Life is active, marked enemies that are killed will produce an orb.
                        """,
                50000,
                () -> {
                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.pve.upgrades.*;

public class UndyingArmyBranch extends AbstractUpgradeBranch<UndyingArmy> {

    float flatHealing = ability.getFlatHealing();

    public UndyingArmyBranch(AbilityTree abilityTree, UndyingArmy ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Flat Healing";
                    }

                    @Override
                    public void run(float value) {
                        ability.setFlatHealing(flatHealing + value);
                    }
                }, 25f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgradeCooldown(ability)
                .addTo(treeB);

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

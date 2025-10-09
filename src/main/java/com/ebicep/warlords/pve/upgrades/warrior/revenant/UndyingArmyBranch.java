package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.pve.upgrades.*;

public class UndyingArmyBranch extends AbstractUpgradeBranch<UndyingArmy> {

    float flatHealing = ability.getFlatHealing();

    public UndyingArmyBranch(AbilityTree abilityTree, UndyingArmy ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Flat Healing";
                    }

                    @Override
                    public void run(float value) {
                        ability.setFlatHealing(flatHealing + value);
                    }
                }, 50f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Relentless Army",
                "Undying Army - Master Upgrade",
                """
                        Double the range of Undying Army. Undying Army healing occurs every 0.5 seconds instead of 1 second.
                        
                        Additionally, reduce the Cooldown of Reckless Charge and Ground Slam by 50% for the duration of Undying Army.""",
                50000,
                () -> {
                    ability.setRadius(ability.getRadius() * 2);
                    ability.setHealPeriod(10);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Vengeful Army",
                "Undying Army - Master Upgrade",
                """
                        Upon casting, enemies within the radius are marked for the duration of Undying Army.
                        Marked enemies build up stacks of Vengeance that accrue every second, each stack equals 200hp.
                        After 10s marked enemies pop taking damage based on stacks accrued as well as 200% of MAX HEALTH DAMAGE.
                        If Orbs of Life is active, marked enemies that are killed will produce an orb.
                        """,
                50000,
                () -> {
                }
        );
    }
}

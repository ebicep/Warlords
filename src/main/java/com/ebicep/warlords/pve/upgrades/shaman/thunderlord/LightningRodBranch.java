package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.LightningRod;
import com.ebicep.warlords.pve.upgrades.*;

public class LightningRodBranch extends AbstractUpgradeBranch<LightningRod> {

    int healthRestore = ability.getHealthRestore();
    int energyRestore = ability.getEnergyRestore();

    public LightningRodBranch(AbilityTree abilityTree, LightningRod ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Energy Given";
                    }

                    @Override
                    public void run(float value) {
                        ability.setEnergyRestore(energyRestore + (int) value);
                    }
                }, 10f)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        ability.setHealthRestore(healthRestore + (int) value);
                    }
                }, 6f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Thunderbolt",
                "Lightning Rod - Master Upgrade",
                "Lightning Rod increases speed and damage dealt by 40% for 12 seconds after initial cast.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Call of Thunder",
                "Lightning Rod - Master Upgrade",
                """
                        All enemies pushed away from Lightning Rod take 25% increased incoming damage from all sources and the caster will gain +15 EPS for 8s.
                        """,
                50000,
                () -> {

                }
        );
    }


}

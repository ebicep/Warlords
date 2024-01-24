package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.LightningRod;
import com.ebicep.warlords.pve.upgrades.*;

public class LightningRodBranch extends AbstractUpgradeBranch<LightningRod> {

    int healthRestore = ability.getHealthRestore();
    int energyRestore = ability.getEnergyRestore();

    public LightningRodBranch(AbilityTree abilityTree, LightningRod ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
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
                .create(abilityTree, this)
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
                """
                        Upon casting Lightning Rod, gain a 40% buff to speed and damage for 12 seconds.
                        Additionally, Lightning Rod will activate every 2s for a duration of 6s. The additional activations will only deal knockback, heal, and restore energy.
                        """,
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Call of Thunder",
                "Lightning Rod - Master Upgrade",
                """
                        Lightning Rod now stuns enemies for 3s instead of knocking them away. All enemies stunned from Lightning Rod take 25% increased incoming damage from all sources and the caster will gain +15 EPS for 8s.
                        """,
                50000,
                () -> {

                }
        );
    }


}

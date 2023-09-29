package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilities.ChainHeal;
import com.ebicep.warlords.pve.upgrades.*;

public class ChainHealBranch extends AbstractUpgradeBranch<ChainHeal> {

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    public ChainHealBranch(AbilityTree abilityTree, ChainHeal ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minHealing * value);
                        ability.setMaxDamageHeal(maxHealing * value);
                    }
                }, 10f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability)
                .addUpgradeCooldown(ability, 3, 4)
                .addTo(treeB);


        masterUpgrade = new Upgrade(
                "Chain of Eradication",
                "Chain Heal - Master Upgrade",
                "+5 Blocks cast and bounce range.\n\nIncrease the Crit Chance by 20% and Crit Multiplier by 40% of all healed allies for 8 seconds.",
                50000,
                () -> {

                    ability.setBounceRange(ability.getBounceRange() + 5);
                    ability.setRadius(ability.getRadius() + 5);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Chains of Blessings",
                "Chain Heal - Master Upgrade",
                """
                        Allies healed by chain will be connected to the caster for 5 seconds. All connected players gain 10 energy per second and are healed every second for 2.5% of the caster max HP.
                        """,
                50000,
                () -> {
                }
        );
    }
}

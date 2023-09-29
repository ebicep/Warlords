package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.AvengersWrath;
import com.ebicep.warlords.pve.upgrades.*;

public class AvengersWrathBranch extends AbstractUpgradeBranch<AvengersWrath> {

    float energyPerSecond = ability.getEnergyPerSecond();
    int duration = ability.getTickDuration();

    public AvengersWrathBranch(AbilityTree abilityTree, AvengersWrath ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.EnergyUpgradeType() {

                                @Override
                                public String getDescription0(String value) {
                                    return "+" + value + " Energy per Second";
                                }

                                @Override
                                public void run(float value) {
                                    ability.setEnergyPerSecond(energyPerSecond + value);
                                }
                            },
                        2.5f
                )
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 40f)
                .addTo(treeB);


        masterUpgrade = new Upgrade(
                "Avenger’s Armageddon",
                "Avenger's Wrath - Master Upgrade",
                "-10% Cooldown Reduction\n\nAvenger's Wrath hits 3 additional targets and double the hit radius.",
                50000,
                () -> {
                    ability.setHitRadius(ability.getHitRadius() * 2);
                    ability.setMaxTargets(ability.getMaxTargets() + 3);
                    ability.getCooldown().addMultiplicativeModifierMult("Avenger's Armageddon", 0.9f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Avenger's Vexation",
                "Avenger's Wrath - Master Upgrade",
                """
                        -10% Cooldown Reduction
                                                
                        Strikes cast during Avenger's Wrath hit twice per opponent.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Avenger's Vexation", 0.9f);
                }
        );
    }
}

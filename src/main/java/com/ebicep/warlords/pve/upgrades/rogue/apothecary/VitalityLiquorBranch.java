package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.VitalityLiquor;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class VitalityLiquorBranch extends AbstractUpgradeBranch<VitalityLiquor> {

    int energyPerSecond = ability.getEnergyPerSecond();
    float minWaveHealing = ability.getMinWaveHealing();
    float maxWaveHealing = ability.getMaxWaveHealing();
    int vitalityRange = ability.getVitalityRange();

    public VitalityLiquorBranch(AbilityTree abilityTree, VitalityLiquor ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinWaveHealing(minWaveHealing * v);
                        ability.setMaxWaveHealing(maxWaveHealing * v);
                    }

                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value);
                    }
                }, 10f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgrade(
                        new UpgradeTypes.UpgradeType() {
                            @Override
                            public String getDescription0(String value) {
                                return "+" + value + " Energy per Second";
                            }

                            @Override
                            public void run(float value) {
                                ability.setEnergyPerSecond(energyPerSecond + (int) value);
                            }
                        },
                        1f
                )
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Liquor Of Life",
                "Vitality Liquor - Master Upgrade",
                "Double the impact range and the duration of the bonus energy per second of Vitality Liquor. Additionally, all enemies hit are slowed by 30% for 3 seconds.",
                50000,
                () -> {
                    ability.setVitalityRange(vitalityRange * 2);
                    ability.setDuration(ability.getDuration() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Medicinal Brew",
                "Vitality Liquor - Master Upgrade",
                """
                        Allies healed by Vitality, including the Apothecary, are given 30% speed and double energy duration.
                        """,
                50000,
                () -> {
                    ability.setDuration(ability.getDuration() * 2);
                }
        );
    }
}

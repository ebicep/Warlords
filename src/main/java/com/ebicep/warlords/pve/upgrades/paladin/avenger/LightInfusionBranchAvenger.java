package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.LightInfusionAvenger;
import com.ebicep.warlords.pve.upgrades.*;

public class LightInfusionBranchAvenger extends AbstractUpgradeBranch<LightInfusionAvenger> {

    float speedBuff = ability.getSpeedBuff();
    int energyGiven = ability.getEnergyGiven();

    public LightInfusionBranchAvenger(AbilityTree abilityTree, LightInfusionAvenger ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.EnergyUpgradeType() {

                                @Override
                                public String getDescription0(String value) {
                                    return "+" + value + " Energy Given";
                                }

                                @Override
                                public void run(float value) {
                                    ability.setEnergyGiven((int) (energyGiven + value));
                                }
                            },
                        10f
                )
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability, .075f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+20% Speed";
                    }

                    @Override
                    public void run(float value) {
                        ability.setSpeedBuff(speedBuff + 20);
                    }
                }, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Holy Imbusion",
                "Light Infusion - Master Upgrade",
                "Each Avenger's Strike cast while Light Infusion is active will refund 30 energy when Light Infusion ends.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Stellar Light",
                "Light Infusion - Master Upgrade",
                """
                        +2s Duration
                        
                        For the duration of Light Infusion gain +10 energy per second and +20 energy per hit.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 200);
                }
        );
    }
}

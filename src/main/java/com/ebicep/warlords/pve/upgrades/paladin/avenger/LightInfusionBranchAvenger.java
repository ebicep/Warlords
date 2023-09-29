package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.LightInfusionAvenger;
import com.ebicep.warlords.pve.upgrades.*;

public class LightInfusionBranchAvenger extends AbstractUpgradeBranch<LightInfusionAvenger> {

    int speedBuff = ability.getSpeedBuff();
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
                        Casting Light Infusion near allies in a 5 block radius, will grant them a 10% damage bonus for 5s and for every ally you grant this buff, increase the duration by 1s.
                        """,
                50000,
                () -> {

                }
        );
    }
}

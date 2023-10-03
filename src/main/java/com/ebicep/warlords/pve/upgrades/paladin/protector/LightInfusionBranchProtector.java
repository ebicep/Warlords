package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilities.LightInfusionProtector;
import com.ebicep.warlords.pve.upgrades.*;

public class LightInfusionBranchProtector extends AbstractUpgradeBranch<LightInfusionProtector> {

    int energyGiven = ability.getEnergyGiven();

    public LightInfusionBranchProtector(AbilityTree abilityTree, LightInfusionProtector ability) {
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
                        ability.setEnergyGiven(energyGiven + (int) value);
                    }
                }, 15f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgrade(
                        new UpgradeTypes.UpgradeType() {
                            @Override
                            public String getDescription0(String value) {
                                return "+" + value + "% Speed";
                            }

                            @Override
                            public void run(float value) {
                                ability.setSpeedBuff((int) (ability.getSpeedBuff() + value));
                            }
                        }, 20f, 4
                )
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Ornament of Light",
                "Light Infusion - Master Upgrade",
                "Gain 90% damage reduction and 50% knockback resistance and immunity to de-buffs for 4 seconds and reset Holy Radiance's cooldown on cast.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Chiron Light",
                "Light Infusion - Master Upgrade",
                """
                        Casting Light Infusion near allies in a 5 block radius, will grant them infusion's speed buff and immunity to all debuffs for 4s. Strike healing during the duration of Light Infusion is increased by 25%.
                        """,
                50000,
                () -> {

                }
        );
    }
}

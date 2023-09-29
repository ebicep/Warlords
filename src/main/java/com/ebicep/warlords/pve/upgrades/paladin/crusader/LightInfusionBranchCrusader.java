package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilities.LightInfusionCrusader;
import com.ebicep.warlords.pve.upgrades.*;

public class LightInfusionBranchCrusader extends AbstractUpgradeBranch<LightInfusionCrusader> {

    int speedBuff = ability.getSpeedBuff();
    int energyGiven = ability.getEnergyGiven();

    public LightInfusionBranchCrusader(AbilityTree abilityTree, LightInfusionCrusader ability) {
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
                }, 5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Speed";
                    }

                    @Override
                    public void run(float value) {
                        ability.setSpeedBuff(speedBuff + 20);
                    }
                }, 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Holy Imbusion",
                "Light Infusion - Master Upgrade",
                "+3s Duration\n\nCasting Light Infusion near other allies will give them Light Infusion. (Half the energy given.)",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 60);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Proxima Light",
                "Light Infusion - Master Upgrade",
                """
                        +2s Duration. Casting Light Infusion near allies will reduce their cooldowns by 2s.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 40);

                }
        );
    }
}

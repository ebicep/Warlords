package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.LightInfusionAvenger;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightInfusionBranchAvenger extends AbstractUpgradeBranch<LightInfusionAvenger> {

    int speedBuff = ability.getSpeedBuff();
    float cooldown = ability.getCooldown();
    int energyGiven = ability.getEnergyGiven();

    public LightInfusionBranchAvenger(AbilityTree abilityTree, LightInfusionAvenger ability) {

        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "+10 Energy given",
                5000,
                () -> {
                    ability.setEnergyGiven(energyGiven + 10);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "+20 Energy given",
                10000,
                () -> {
                    ability.setEnergyGiven(energyGiven + 20);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "+30 Energy given",
                15000,
                () -> {
                    ability.setEnergyGiven(energyGiven + 30);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "+40 Energy given",
                20000,
                () -> {
                    ability.setEnergyGiven(energyGiven + 40);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-7.5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.925f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-15% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-22.5% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.775f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-30% Cooldown reduction\n+20% Speed",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.7f);
                    ability.setSpeedBuff(speedBuff + 20);
                }
        ));

        masterUpgrade = new Upgrade(
                "Holy Imbusion",
                "Light Infusion - Master Upgrade",
                "Each Avenger's Strike cast while Light Infusion is active will refund 30 energy when Light Infusion ends.",
                50000,
                () -> {
                    ability.setPveMasterUpgrade(true);
                }
        );
    }
}

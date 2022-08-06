package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilties.LightInfusionAvenger;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightInfusionAvengerBranch extends AbstractUpgradeBranch<LightInfusionAvenger> {

    int speedBuff = ability.getSpeedBuff();
    float cooldown = ability.getCooldown();
    int duration = ability.getDuration();

    public LightInfusionAvengerBranch(AbilityTree abilityTree, LightInfusionAvenger ability) {

        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "+1.5s Duration",
                2500,
                () -> {
                    ability.setDuration(duration + 1);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "+3s Duration",
                5000,
                () -> {
                    ability.setDuration(duration + 3);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "+4.5s Duration",
                7500,
                () -> {
                    ability.setDuration(duration + 5);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "+6s Duration\n+20 Energy given",
                10000,
                () -> {
                    ability.setDuration(duration + 6);
                    ability.setEnergyGiven(ability.getEnergyGiven() + 20);
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
                "Each Avenger's Strike casted while\nLight Infusion is active will refund 30 energy\nwhen Light Infusion ends.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}

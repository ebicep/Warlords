package com.ebicep.warlords.pve.upgrades.paladin;

import com.ebicep.warlords.abilties.LightInfusion;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightInfusionBranch extends AbstractUpgradeBranch<LightInfusion> {

    int speedBuff = ability.getSpeedBuff();
    float cooldown = ability.getCooldown();

    public LightInfusionBranch(AbilityTree abilityTree, LightInfusion ability) {

        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "+5% Speed",
                2500,
                () -> {
                    ability.setSpeedBuff(speedBuff + 5);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "+10% Speed",
                5000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 10);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "+15% Speed",
                7500,
                () -> {
                    ability.setSpeedBuff(speedBuff + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "+20% Speed\n+15 Energy given",
                10000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 20);
                    ability.setEnergyGiven(ability.getEnergyGiven() + 15);
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
                "-30% Cooldown reduction\n+2s Duration",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.7f);
                    ability.setDuration(ability.getDuration() + 3);
                }
        ));

        masterUpgrade = new Upgrade(
                "Holy Imbusion",
                "Light Infusion - Master Upgrade",
                "Each Avenger's Strike casted in the last\n3 seconds will increase the energy given\nfrom Light Infusion by 30\n\nWarning: WIP",
                50000,
                () -> {

                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilties.LightInfusionCrusader;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightInfusionBranchCrusader extends AbstractUpgradeBranch<LightInfusionCrusader> {

    float cooldown = ability.getCooldown();
    int speedBuff = ability.getSpeedBuff();
    int energyGiven = ability.getEnergyGiven();

    public LightInfusionBranchCrusader(AbilityTree abilityTree, LightInfusionCrusader ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+5% Speed\n+5 Energy given",
                5000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 5);
                    ability.setEnergyGiven(energyGiven + 5);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+10% Speed\n+10 Energy given",
                10000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 10);
                    ability.setEnergyGiven(energyGiven + 10);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+15% Speed\n+15 Energy given",
                15000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 15);
                    ability.setEnergyGiven(energyGiven + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+20% Speed\n+20 Energy given",
                20000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 20);
                    ability.setEnergyGiven(energyGiven + 20);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Holy Imbusion",
                "Light Infusion - Master Upgrade",
                "+3s Duration\n\nCasting Light Infusion near other allies will give\nthem Light Infusion. (Half the energy given.)",
                50000,
                () -> {
                    ability.setDuration(ability.getDuration() + 3);
                    ability.setPveUpgrade(true);
                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilities.LightInfusionProtector;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightInfusionBranchProtector extends AbstractUpgradeBranch<LightInfusionProtector> {

    int energyGiven = ability.getEnergyGiven();
    float cooldown = ability.getCooldown();

    public LightInfusionBranchProtector(AbilityTree abilityTree, LightInfusionProtector ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "+15 Energy given",
                5000,
                () -> {
                    ability.setEnergyGiven(energyGiven + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "+30 Energy given",
                10000,
                () -> {
                    ability.setEnergyGiven(energyGiven + 30);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "+45 Energy given",
                15000,
                () -> {
                    ability.setEnergyGiven(energyGiven + 45);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "+60 Energy given",
                20000,
                () -> {
                    ability.setEnergyGiven(energyGiven + 60);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20% Cooldown reduction\n+20% Speed",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                    ability.setSpeedBuff(ability.getSpeedBuff() + 20);
                }
        ));

        masterUpgrade = new Upgrade(
                "Ornament of Light",
                "Light Infusion - Master Upgrade",
                "Gain 90% damage reduction and 50% knockback resistance and immunity to de-buffs for 4 seconds and reset Holy Radiance's cooldown on cast.",
                50000,
                () -> {

                }
        );
    }
}

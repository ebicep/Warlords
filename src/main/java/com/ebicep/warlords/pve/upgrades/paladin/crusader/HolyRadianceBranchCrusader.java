package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilities.HolyRadianceCrusader;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class HolyRadianceBranchCrusader extends AbstractUpgradeBranch<HolyRadianceCrusader> {

    public HolyRadianceBranchCrusader(AbilityTree abilityTree, HolyRadianceCrusader ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(getAbility().getRadianceHealing(), 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Edifying Radiance",
                "Holy Radiance - Master Upgrade",
                "Crusader's Holy Mark provides triple the energy per second and speed at the cost of increased energy cost.",
                50000,
                () -> {
                    ability.setEnergyPerSecond(ability.getEnergyPerSecond() * 3);
                    ability.setMarkSpeed(ability.getMarkSpeed() * 3);
                    ability.getEnergyCost().addMultiplicativeModifierMult("Master Upgrade Branch", 3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Unrivalled Radiance",
                "Holy Radiance - Master Upgrade",
                """
                        Crusader's Holy Mark provides 10 more energy per second and will have the energy cost of their abilities decreased by 10.
                        """,
                50000,
                () -> {
                    ability.setEnergyPerSecond(ability.getEnergyPerSecond() + 10);
                }
        );
    }
}

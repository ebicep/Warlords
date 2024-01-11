package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilities.HolyRadianceCrusader;
import com.ebicep.warlords.pve.upgrades.*;

public class HolyRadianceBranchCrusader extends AbstractUpgradeBranch<HolyRadianceCrusader> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public HolyRadianceBranchCrusader(AbilityTree abilityTree, HolyRadianceCrusader ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * value);
                        ability.setMaxDamageHeal(maxDamage * value);
                    }
                }, 7.5f)
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

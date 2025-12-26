package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilities.LightInfusionProtector;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

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
                                ability.setSpeedBuff(ability.getSpeedBuff() + value);
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
                "Ornament of Darkness",
                "Light Infusion - Master Upgrade",
                """
                        +100% Cooldown increase
                        +9s Duration
                        
                        Fall into darkness, causing your healing to be reduced by 80% but all your attacks (excluding Consecrate) grant you a stack of corruption. Upon reactivating the ability, consume all stacks to increase your damage by 5% per stack (max 200%) for 3 seconds.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Ornament of Darkness", 2f);
                    ability.setTickDuration(ability.getTickDuration() + 180);
                }
        );
    }
}

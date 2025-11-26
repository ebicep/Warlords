package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.EnergySeerSentinel;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class EnergySeerBranchSentinel extends AbstractUpgradeBranch<EnergySeerSentinel> {


    public EnergySeerBranchSentinel(AbilityTree abilityTree, EnergySeerSentinel ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 10f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value / 100);
                    }
                            }, ability.getHealValues().getSeerHealingMultiplier().value().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Upgrade Branch", 0), 25f
                )
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Energizing Clairvoyant",
                "Energy Seer - Master Upgrade",
                """
                        Remove energy loss.
                        Increase damage reduction by 3% and double the energy restored.
                        """,
                50000,
                () -> {
                    ability.setEpsDecrease(0);
                    ability.setDamageResistance(ability.getDamageResistance() + 3);
                    ability.setEnergyRestore(ability.getEnergyRestore() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Collective Vaticinator",
                "Energy Seer - Master Upgrade",
                """
                        Remove energy loss.
                        +6s duration.
                        +25% Additional Cooldown Reduction
                        """,
                50000,
                () -> {
                    ability.setEpsDecrease(0);
                    ability.setTickDuration(ability.getTickDuration() + 120);
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Collective Vaticinator", 0.75f);
                }
        );
    }

}

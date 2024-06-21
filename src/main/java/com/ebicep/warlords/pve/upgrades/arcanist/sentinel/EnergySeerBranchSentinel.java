package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.EnergySeerSentinel;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class EnergySeerBranchSentinel extends AbstractUpgradeBranch<EnergySeerSentinel> {


    public EnergySeerBranchSentinel(AbilityTree abilityTree, EnergySeerSentinel ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value / 100);
                    }
                }, ability.getHealMultiplier().value().addAdditiveModifier("Upgrade Branch", 0), 25f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability::setBonusDuration, ability::getBonusDuration, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Energizing Clairvoyant",
                "Energy Seer - Master Upgrade",
                """
                        Increase damage reduction by 15% and triple the energy restored.
                        """,
                50000,
                () -> {
                    ability.setDamageResistance(ability.getDamageResistance() + 15);
                    ability.setEnergyRestore(ability.getEnergyRestore() * 3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Collective Vaticinator",
                "Energy Seer - Master Upgrade",
                """
                        +20% Additional Cooldown Reduction
                                                
                        When Energy Seer expires, apply the benefits to all nearby allies within a 10 block radius.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Collective Vaticinator", 0.8f);
                }
        );
    }

}

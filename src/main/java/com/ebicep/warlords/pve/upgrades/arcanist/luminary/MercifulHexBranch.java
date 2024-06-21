package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.MercifulHex;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class MercifulHexBranch extends AbstractUpgradeBranch<MercifulHex> {

    public MercifulHexBranch(AbilityTree abilityTree, MercifulHex ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability.getHealValues(), 15f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeDamage(ability.getDamageValues().getHexDamage(), 15f, 3, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Benevolent Hex",
                "Merciful Hex - Master Upgrade",
                """
                        All allies hit receive 1 extra stack of Merciful Hex. Increase additional targets hit healing/damage by 20%.
                        """,
                50000,
                () -> {
                    ability.setSubsequentReduction(ability.getSubsequentReduction() + 20);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Rainbow Hex",
                "Merciful Hex - Master Upgrade",
                """
                        -15 Energy cost
                                                
                        Merciful Hex healing occurs every .5s instead of 2s.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -15);
                    ability.setTicksBetweenDot(10);
                }
        );
    }

    @Override
    public void runOnce() {
        ability.getDamageValues()
               .getValues()
               .forEach(value -> {
                   value.forEachValue(floatModifiable -> floatModifiable.addMultiplicativeModifierAdd("PvE", .15f));
               });
        ability.getHealValues()
               .getValues()
               .forEach(value -> {
                   value.forEachValue(floatModifiable -> floatModifiable.addMultiplicativeModifierAdd("PvE", .15f));
               });
    }

}

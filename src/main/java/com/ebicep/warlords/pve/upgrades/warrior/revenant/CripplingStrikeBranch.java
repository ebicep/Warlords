package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class CripplingStrikeBranch extends AbstractUpgradeBranch<CripplingStrike> {


    @Override
    public void runOnce() {
        ability.getMinDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
        ability.getMaxDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public CripplingStrikeBranch(AbilityTree abilityTree, CripplingStrike ability) {
        super(abilityTree, ability);
        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Crippling Slash",
                "Crippling Strike - Master Upgrade",
                "Crippling Strike deals damage to 2 additional targets, the cripple status now reduces enemy damage dealt by 50%",
                50000,
                () -> {
                    ability.setCripple(50);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Productive Strike",
                "Crippling Strike - Master Upgrade",
                """
                        -20 Energy cost
                                                
                        Crippling Strikes deals damage to 2 additional targets, Strike kills will reduce the cooldown of Orbs of Life by 0.5s.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -15);
                }
        );
    }
}

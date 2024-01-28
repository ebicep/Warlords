package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.pve.upgrades.*;

public class CripplingStrikeBranch extends AbstractUpgradeBranch<CripplingStrike> {

    float minDamage;
    float maxDamage;

    @Override
    public void runOnce() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);
    }

    public CripplingStrikeBranch(AbilityTree abilityTree, CripplingStrike ability) {
        super(abilityTree, ability);
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();
        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 7.5f)
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

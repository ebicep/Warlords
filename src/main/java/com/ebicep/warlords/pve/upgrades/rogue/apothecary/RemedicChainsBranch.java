package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.RemedicChains;
import com.ebicep.warlords.pve.upgrades.*;

public class RemedicChainsBranch extends AbstractUpgradeBranch<RemedicChains> {

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    public RemedicChainsBranch(AbilityTree abilityTree, RemedicChains ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minHealing * value);
                        ability.setMaxDamageHeal(maxHealing * value);
                    }
                }, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Crystallizing Chains",
                "Remedic Chains - Master Upgrade",
                "Increase bonus damage dealt by an additional 8% and temporarily increase all linked allies' max health by 25%.",
                50000,
                () -> {

                    ability.setAllyDamageIncrease(20);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Counterstrike",
                "Remedic Chains - Master Upgrade",
                """
                        Warriors', Paladins', and Rogues' linked have their strikes infused with LEECH application capability for 5s.
                                                
                        Mages', Shamans', and Arcanists' linked have their EPH doubled for 5s.
                        """,
                50000,
                () -> {

                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.RemedicChains;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class RemedicChainsBranch extends AbstractUpgradeBranch<RemedicChains> {

    @Override
    public void runOnce() {
        ability.getDamageValues().getBonusDamage().value().addAdditiveModifier("PvE (Base)", 80);
    }

    public RemedicChainsBranch(AbilityTree abilityTree, RemedicChains ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability.getHealValues().getChainHealing(), 12.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Crystallizing Chains",
                "Remedic Chains - Master Upgrade",
                """
                        +20 Block link break radius
                        
                        Increase bonus damage dealt by an additional 80 and temporarily increase all linked allies' max health by 25%.""",
                50000,
                () -> {
                    ability.setLinkBreakRadius(ability.getLinkBreakRadius() + 20);
                    ability.getDamageValues().getBonusDamage().value().addAdditiveModifier("Crystallizing Chains", 80);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Counterstrike",
                "Remedic Chains - Master Upgrade",
                """
                        +20 Block link break radius
                        
                        Increase linked ally damage by 15% and their EPS by 5.
                        """,
                50000,
                () -> {
                    ability.setLinkBreakRadius(ability.getLinkBreakRadius() + 20);
                }
        );
    }
}

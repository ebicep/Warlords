package com.ebicep.warlords.pve.upgrades.mage;

import com.ebicep.warlords.abilities.ArcaneShield;
import com.ebicep.warlords.pve.upgrades.*;

public class ArcaneShieldBranch extends AbstractUpgradeBranch<ArcaneShield> {

    int shieldPercentage = ability.getShieldPercentage();

    public ArcaneShieldBranch(AbilityTree abilityTree, ArcaneShield ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.ShieldUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Shield Health";
                    }

                    @Override
                    public void run(float value) {
                        ability.setShieldPercentage(shieldPercentage + (int) value);
                        ability.updateCustomStats(abilityTree.getWarlordsPlayer());
                    }
                }, 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Arcane Aegis",
                "Arcane Shield - Master Upgrade",
                "When arcane shield ends or breaks, unleash a shockwave that stuns enemies for 6 seconds.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Arcane Energy",
                "Arcane Shield - Master Upgrade",
                """
                        When arcane shield ends or breaks, gain the ARC status for 5s reducing the energy cost of Right-Click attacks by 15%.
                        """,
                50000,
                () -> {

                }
        );
    }
}

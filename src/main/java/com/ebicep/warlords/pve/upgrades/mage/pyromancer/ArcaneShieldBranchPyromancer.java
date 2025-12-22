package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilities.ArcaneShieldPyromancer;
import com.ebicep.warlords.pve.upgrades.*;

public class ArcaneShieldBranchPyromancer extends AbstractUpgradeBranch<ArcaneShieldPyromancer> {

    float shieldPercentage = ability.getShieldPercentage();

    public ArcaneShieldBranchPyromancer(AbilityTree abilityTree, ArcaneShieldPyromancer ability) {
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
                        ability.setShieldPercentage(shieldPercentage + value);
                        ability.updateCustomStats(abilityTree.getWarlordsPlayer());
                    }
                }, 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Arcane Energy",
                "Arcane Shield - Master Upgrade",
                """
                        When arcane shield ends or breaks, unleash a shockwave that stuns enemies for 6 seconds within a 6 block radius. Additionally, gain the ARC status for 6 seconds, reducing the energy cost of Right-Click attacks by 25%.
                        """,
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Blazing Anarchy",
                "Arcane Shield - Master Upgrade",
                """
                        +2s Duration
                        
                        While Arcane Shield is active, summon 3 blazing saw blades around you, dealing 175-250 damage. Each hit increases the damage by 3% (max 300%).
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 40);
                }
        );
    }
}

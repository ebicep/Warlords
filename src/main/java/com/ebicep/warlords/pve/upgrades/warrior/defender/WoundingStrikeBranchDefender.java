package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.WoundingStrikeDefender;
import com.ebicep.warlords.pve.upgrades.*;

public class WoundingStrikeBranchDefender extends AbstractUpgradeBranch<WoundingStrikeDefender> {

    float minDamage;
    float maxDamage;

    @Override
    public void runOnce() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);
    }

    public WoundingStrikeBranchDefender(AbilityTree abilityTree, WoundingStrikeDefender ability) {
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
                .addUpgradeEnergy(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Lacerating Strike",
                "Wounding Strike - Master Upgrade",
                "+100% Critical Chance.\n\nCritical Strikes grant you and nearby allies 30% damage reduction for 5 seconds.",
                50000,
                () -> {
                    ability.setCritChance(100);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Shredding Strike",
                "Wounding Strike - Master Upgrade",
                """
                        -20 Energy cost
                                                
                        Wounding Strike now hits up to 2 enemies. Strikes will ignore 100% of enemies resistance (except for bosses).
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -20);
                }
        );
    }
}

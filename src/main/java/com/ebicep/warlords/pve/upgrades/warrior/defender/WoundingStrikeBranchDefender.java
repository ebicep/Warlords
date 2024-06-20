package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.WoundingStrikeDefender;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class WoundingStrikeBranchDefender extends AbstractUpgradeBranch<WoundingStrikeDefender> {

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
        damage.min().addMultiplicativeModifierAdd("PvE", .3f);
        damage.max().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public WoundingStrikeBranchDefender(AbilityTree abilityTree, WoundingStrikeDefender ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 7.5f)
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
                    ability.getDamageValues().getStrikeDamage().critChance().addAdditiveModifier("Master Upgrade Branch", 100);
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

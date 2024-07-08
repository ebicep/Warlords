package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class WoundingStrikeBranchBerserker extends AbstractUpgradeBranch<WoundingStrikeBerserker> {


    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
        damage.min().addMultiplicativeModifierAdd("PvE", .3f);
        damage.max().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public WoundingStrikeBranchBerserker(AbilityTree abilityTree, WoundingStrikeBerserker ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 12.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeDuration(ability::setWoundingTickDuration, ability::getWoundingTickDuration, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Lacerating Strike",
                "Wounding Strike - Master Upgrade",
                """
                        Wounding Strike now applies BLEED instead of wounding.

                        BLEED: Enemies afflicted take 100% more damage from Wounding Strike while Blood Lust is active. Bleeding enemies have healing reduced by 80% and lose 0.5% of their max health per second.""",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Lustful Strike",
                "Wounding Strike - Master Upgrade",
                """
                        -20 Energy cost
                         
                        Wounding Strike now hits up to 2 enemies. Strikes deal 20% more damage while Blood Lust is active.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -20);
                }
        );

    }
}

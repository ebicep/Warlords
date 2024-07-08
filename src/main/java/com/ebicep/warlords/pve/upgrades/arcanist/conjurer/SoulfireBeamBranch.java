package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.SoulfireBeam;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class SoulfireBeamBranch extends AbstractUpgradeBranch<SoulfireBeam> {

    @Override
    public void runOnce() {
        Value.RangedValueCritable hexDamage = ability.getDamageValues().getBeamDamage();
        hexDamage.min().addMultiplicativeModifierAdd("PvE", .3f);
        hexDamage.max().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public SoulfireBeamBranch(AbilityTree abilityTree, SoulfireBeam ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getBeamDamage(), 7.5f)
                .addUpgradeHitBox(ability, 2f, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Eradicating Beam",
                "Soulfire Beam - Master Upgrade",
                """
                        Increase the damage multiplier on the first 4 max stack targets by 500%.
                        """,
                50000,
                () -> {
                }
        );
        masterUpgrade2 = new Upgrade(
                "Volatile Beam",
                "Soulfire Beam - Master Upgrade",
                """
                        +3 Additional Block Radius
                                                
                        Soulfire Beam fires two additional beams.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addAdditiveModifier("Master Upgrade Branch", 3);
                    ability.setShotsFiredAtATime(3);
                }
        );
    }

}

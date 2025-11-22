package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.SoulfireBeam;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

import java.util.ArrayList;
import java.util.List;

public class SoulfireBeamBranch extends AbstractUpgradeBranch<SoulfireBeam> {

    @Override
    public void runOnce() {
        Value.RangedValueCritable hexDamage = ability.getDamageValues().getBeamDamage();
        hexDamage.min().addMultiplicativeModifierAdd("PvE", .3f);
        hexDamage.max().addMultiplicativeModifierAdd("PvE", .3f);
        ability.getDamageValues().setDamageMultipliers(new ArrayList<>(List.of(1.0f, 1.25f, 1.5f, 2.0f, 3f, 4f)));
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
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Eradicating Beam",
                "Soulfire Beam - Master Upgrade",
                """
                        Triple the damage multiplier based on hex stacks.
                        """,
                50000,
                () -> {
                    ability.getDamageValues().getDamageMultipliers().replaceAll(aFloat -> (aFloat - 1) * 3 + 1);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Volatile Beam",
                "Soulfire Beam - Master Upgrade",
                """
                        +1 Additional Block Radius
                        +15 Block range
                        -20 Energy Cost
                        
                        Soulfire Beam fires two additional beams and hit targets receive 1 stack of PHEX.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addAdditiveModifier("Master Upgrade Branch", 1);
                    ability.getMaxDistance().addAdditiveModifier("Master Upgrade Branch", 15);
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -20);
                    ability.setShotsFiredAtATime(3);
                }
        );
    }

}

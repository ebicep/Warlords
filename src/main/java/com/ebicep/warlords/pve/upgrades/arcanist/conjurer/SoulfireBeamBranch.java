package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.SoulfireBeam;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class SoulfireBeamBranch extends AbstractUpgradeBranch<SoulfireBeam> {

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
                        Increase the damage multiplier on the first 8 max stack targets by 500%.
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
                        +15 Block range
                        
                        Soulfire Beam fires two additional beams, additionally double the damage increase based on hex stacks.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 3);
                    ability.getMaxDistance().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 15);
                    ability.setShotsFiredAtATime(3);
                    ability.getDamageValues().getDamageMultipliers().replaceAll(aFloat -> aFloat * 2);
                }
        );
    }

    @Override
    public void runOnce() {
        Value.RangedValueCritable hexDamage = ability.getDamageValues().getBeamDamage();
        hexDamage.min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
        hexDamage.max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
    }

}

package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.MysticalBarrier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class MysticalBarrierBranch extends AbstractUpgradeBranch<MysticalBarrier> {

    public MysticalBarrierBranch(AbilityTree abilityTree, MysticalBarrier ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Transcendent Barrier",
                "Mystical Barrier - Master Upgrade",
                """
                        +3s Duration
                        +20% Additional Cooldown Reduction
                        
                        Draw aggro from nearby mobs when activating Mystical Barrier. Increase max shield health by 3000 and increase amount of shield granted for each damage instance by 80.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Transcendent Barrier", 0.8f);
                    ability.setShieldMaxHealth(ability.getShieldMaxHealth() + 3000);
                    ability.setShieldIncrease(ability.getShieldIncrease() + 80);
                    ability.setTickDuration(ability.getTickDuration() + 60);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Illusory Barrier",
                "Mystical Barrier - Master Upgrade",
                """
                        Mystical Barrier, will now grant yourself and nearby allies the shield. Players under the effect of Mystical Barrier receive 3x more shields from Guardian Beam.
                        """,
                50000,
                () -> {
                    ability.setGuardianBeamShieldMultiplier(3);
                }
        );
    }

}

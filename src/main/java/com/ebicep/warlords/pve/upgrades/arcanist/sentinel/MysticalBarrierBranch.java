package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.MysticalBarrier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class MysticalBarrierBranch extends AbstractUpgradeBranch<MysticalBarrier> {


    @Override
    public void runOnce() {
        ability.setRuneTimerIncrease(0.5f);
    }

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
                        +20% Additional Cooldown Reduction
                                                
                        Increase max shield health by 2000 and increase amount of shield granted for each damage instance by 120.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Transcendent Barrier", 0.8f);
                    ability.setShieldMaxHealth(ability.getShieldMaxHealth() + 2000);
                    ability.setShieldIncrease(ability.getShieldIncrease() + 120);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Illusory Barrier",
                "Mystical Barrier - Master Upgrade",
                """
                        Reactivating Mystical Barrier, will now grant yourself and all nearby allies the shield. Not reactivating the ability will reduce its cooldown by 35%.
                        """,
                50000,
                () -> {
                }
        );
    }

}

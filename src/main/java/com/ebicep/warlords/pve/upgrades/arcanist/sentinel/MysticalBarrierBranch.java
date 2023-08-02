package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.MysticalBarrier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class MysticalBarrierBranch extends AbstractUpgradeBranch<MysticalBarrier> {

    float cooldown = ability.getCooldown();
    int tickDuration = ability.getReactivateTickDuration();

    public MysticalBarrierBranch(AbilityTree abilityTree, MysticalBarrier ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Chronos - Tier I",
                "+0.5s Duration",
                5000,
                () -> {
                    ability.setReactivateTickDuration(tickDuration + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+1s Duration",
                10000,
                () -> {
                    ability.setReactivateTickDuration(tickDuration + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+1.5s Duration",
                15000,
                () -> {
                    ability.setReactivateTickDuration(tickDuration + 30);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+2s Duration",
                20000,
                () -> {
                    ability.setReactivateTickDuration(tickDuration + 40);
                }
        ));

        masterUpgrade = new Upgrade(
                "Transcendent Barrier",
                "Mystical Barrier - Master Upgrade",
                """
                        -20% Cooldown reduction. Increase max shield health by 2000 and increase amount of shield granted for each damage instance by 120.
                        """,
                50000,
                () -> {
                    ability.setCooldown(ability.getCooldown() * 0.8f);
                    ability.setShieldMaxHealth(ability.getShieldMaxHealth() + 2000);
                    ability.setShieldIncrease(ability.getShieldIncrease() + 120);
                }
        );
    }

}

package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilties.IceBarrier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class IceBarrierBranch extends AbstractUpgradeBranch<IceBarrier> {

    int duration = ability.getTickDuration();
    float damageReductionPercent = ability.getDamageReductionPercent();

    public IceBarrierBranch(AbilityTree abilityTree, IceBarrier ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "+7.5% Damage reduction",
                5000,
                () -> {
                    ability.setDamageReductionPercent(damageReductionPercent + 7.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "+15% Damage reduction",
                10000,
                () -> {
                    ability.setDamageReductionPercent(damageReductionPercent + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "+22.5% Damage reduction",
                15000,
                () -> {
                    ability.setDamageReductionPercent(damageReductionPercent + 22.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "+30% Damage reduction\n-10% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(ability.getCooldown() * 0.9f);
                    ability.setDamageReductionPercent(damageReductionPercent + 30);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1s Duration",
                5000,
                () -> {
                    ability.setTickDuration(duration + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2s Duration",
                10000,
                () -> {
                    ability.setTickDuration(duration + 40);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3s Duration",
                15000,
                () -> {
                    ability.setTickDuration(duration + 60);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4s Duration\n+20% Slowness on melee hit.",
                20000,
                () -> {
                    ability.setTickDuration(duration + 80);
                    ability.setSlownessOnMeleeHit(ability.getSlownessOnMeleeHit() + 20);
                }
        ));

        masterUpgrade = new Upgrade(
                "Aggravating Hailstorm",
                "Ice Barrier - Master Upgrade",
                "Surround yourself in a glacial super shield, slowing all nearby enemies by 80% for 1 second." +
                        " Additionally, reduce their damage reduction by 1% for every 0.25 seconds in your glacial shield.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}

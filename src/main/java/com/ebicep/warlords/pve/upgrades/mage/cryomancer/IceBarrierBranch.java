package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilties.IceBarrier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class IceBarrierBranch extends AbstractUpgradeBranch<IceBarrier> {

    int duration = ability.getDuration();
    int damageReductionPercent = ability.getDamageReductionPercent();

    public IceBarrierBranch(AbilityTree abilityTree, IceBarrier ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "+10% Damage reduction",
                5000,
                () -> {
                    ability.setDamageReductionPercent(damageReductionPercent + 10);
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
                "+20% Damage reduction",
                15000,
                () -> {
                    ability.setDamageReductionPercent(damageReductionPercent + 20);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "+25% Damage reduction\n-10% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(ability.getCooldown() * 0.9f);
                    ability.setDamageReductionPercent(damageReductionPercent + 25);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+2s Duration",
                5000,
                () -> {
                    ability.setDuration(duration + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+4s Duration",
                10000,
                () -> {
                    ability.setDuration(duration + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+6s Duration",
                15000,
                () -> {
                    ability.setDuration(duration + 6);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+8s Duration\n+20% Slowness on melee hit.",
                20000,
                () -> {
                    ability.setDuration(duration + 8);
                    ability.setSlownessOnMeleeHit(ability.getSlownessOnMeleeHit() + 20);
                }
        ));

        masterUpgrade = new Upgrade(
                "Aggravating Hailstorm",
                "Ice Barrier - Master Upgrade",
                "Surround yourself in a glacial super shield,\nslowing all nearby enemies by 90% for 1 second.\nAdditionally, reduce their damage reduction\nby 3% for each second in your glacial shield.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}

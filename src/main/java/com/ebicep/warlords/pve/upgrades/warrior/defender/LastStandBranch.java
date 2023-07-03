package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.LastStand;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LastStandBranch extends AbstractUpgradeBranch<LastStand> {

    float selfDamageReduction = ability.getSelfDamageReduction();
    float allyDamageReduction;
    int duration = ability.getTickDuration();

    public LastStandBranch(AbilityTree abilityTree, LastStand ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.setTeammateDamageReductionPercent(40);
        }
        allyDamageReduction = ability.getTeammateDamageReduction();

        treeA.add(new Upgrade(
                "Fortify - Tier I",
                "+3% Damage reduction",
                5000,
                () -> {
                    ability.setSelfDamageReductionPercent((int) (selfDamageReduction + 3));
                    ability.setTeammateDamageReductionPercent((int) (allyDamageReduction + 3));
                }
        ));
        treeA.add(new Upgrade(
                "Fortify - Tier II",
                "+6% Damage reduction",
                10000,
                () -> {
                    ability.setSelfDamageReductionPercent((int) (selfDamageReduction + 6));
                    ability.setTeammateDamageReductionPercent((int) (allyDamageReduction + 6));
                }
        ));
        treeA.add(new Upgrade(
                "Fortify - Tier III",
                "+9% Damage reduction",
                15000,
                () -> {
                    ability.setSelfDamageReductionPercent((int) (selfDamageReduction + 9));
                    ability.setTeammateDamageReductionPercent((int) (allyDamageReduction + 9));
                }
        ));
        treeA.add(new Upgrade(
                "Fortify - Tier IV",
                "+12% Damage reduction",
                20000,
                () -> {
                    ability.setSelfDamageReductionPercent((int) (selfDamageReduction + 12));
                    ability.setTeammateDamageReductionPercent((int) (allyDamageReduction + 12));
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
                "+4s Duration",
                20000,
                () -> {
                    ability.setTickDuration(duration + 80);
                }
        ));

        masterUpgrade = new Upgrade(
                "Final Stand",
                "Last Stand - Master Upgrade",
                "Doubles the radius of Last Stand and enemies within half the radius will target you on cast, can be re-casted once. Reduce cooldown by 20%.",
                50000,
                () -> {

                    ability.setCooldown(ability.getCooldown() * 0.8f);
                    ability.setRadius(ability.getRadius() * 2);
                }
        );
    }
}

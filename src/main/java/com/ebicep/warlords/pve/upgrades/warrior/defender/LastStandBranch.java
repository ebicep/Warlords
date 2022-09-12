package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilties.LastStand;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LastStandBranch extends AbstractUpgradeBranch<LastStand> {

    float selfDamageReduction = ability.getSelfDamageReduction();
    float allyDamageReduction = ability.getTeammateDamageReduction();
    int duration = ability.getSelfDuration();
    int allyDuration = ability.getAllyDuration();

    public LastStandBranch(AbilityTree abilityTree, LastStand ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+3% Damage reduction",
                5000,
                () -> {
                    ability.setSelfDamageReductionPercent((int) (selfDamageReduction + 3));
                    ability.setTeammateDamageReductionPercent((int) (allyDamageReduction + 3));
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+6% Damage reduction",
                10000,
                () -> {
                    ability.setSelfDamageReductionPercent((int) (selfDamageReduction + 6));
                    ability.setTeammateDamageReductionPercent((int) (allyDamageReduction + 6));
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+9% Damage reduction",
                15000,
                () -> {
                    ability.setSelfDamageReductionPercent((int) (selfDamageReduction + 9));
                    ability.setTeammateDamageReductionPercent((int) (allyDamageReduction + 9));
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
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
                    ability.setAllyDuration(allyDuration + 1);
                    ability.setSelfDuration(duration + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2s Duration",
                10000,
                () -> {
                    ability.setAllyDuration(allyDuration + 2);
                    ability.setSelfDuration(duration + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3s Duration",
                15000,
                () -> {
                    ability.setAllyDuration(allyDuration + 3);
                    ability.setSelfDuration(duration + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4s Duration",
                20000,
                () -> {
                    ability.setAllyDuration(allyDuration + 4);
                    ability.setSelfDuration(duration + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Final Stand",
                "Last Stand - Master Upgrade",
                "Enemies within the Last Stand radius get\npushed outwards with heavy force on\ncast. Reduce cooldown by 25%.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setCooldown(ability.getCooldown() * 0.75f);
                }
        );
    }
}

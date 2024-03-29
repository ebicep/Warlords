package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilities.Berserk;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BerserkBranch extends AbstractUpgradeBranch<Berserk> {

    float damageBoost = ability.getDamageIncrease();
    int speedBuff = ability.getSpeedBuff();
    int duration = ability.getTickDuration();

    public BerserkBranch(AbilityTree abilityTree, Berserk ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage increase",
                5000,
                () -> {
                    ability.setDamageIncrease(damageBoost + 7.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage increase",
                10000,
                () -> {
                    ability.setDamageIncrease(damageBoost + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage increase",
                15000,
                () -> {
                    ability.setDamageIncrease(damageBoost + 22.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage increase",
                20000,
                () -> {
                    ability.setDamageIncrease(damageBoost + 30);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+3% Speed\n+1s Duration",
                5000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 3);
                    ability.setTickDuration(duration + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+6% Speed\n+2s Duration",
                10000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 6);
                    ability.setTickDuration(duration + 40);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+9% Speed\n+3s Duration",
                15000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 9);
                    ability.setTickDuration(duration + 60);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+12% Speed\n+4s Duration",
                20000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 12);
                    ability.setTickDuration(duration + 80);
                }
        ));

        masterUpgrade = new Upgrade(
                "Maniacal Rage",
                "Berserk - Master Upgrade",
                """
                        +10% Additional damage increase

                        Gain 0.2% Crit chance and Crit Multiplier for each instance of damage you deal to an enemy while Berserk is active. (Max 50%)""",
                50000,
                () -> {
                    ability.setDamageIncrease(ability.getDamageIncrease() + 10);

                }
        );
    }
}

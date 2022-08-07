package com.ebicep.warlords.pve.upgrades.berserker;

import com.ebicep.warlords.abilties.Berserk;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BerserkBranch extends AbstractUpgradeBranch<Berserk> {

    float damageBoost = ability.getDamageIncrease();
    int speedBuff = ability.getSpeedBuff();
    int duration = ability.getDuration();

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
                    ability.setDuration(duration + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+6% Speed\n+2s Duration",
                10000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 3);
                    ability.setDuration(duration + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+9% Speed\n+3s Duration",
                15000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 3);
                    ability.setDuration(duration + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+12% Speed\n+4s Duration",
                20000,
                () -> {
                    ability.setSpeedBuff(speedBuff + 3);
                    ability.setDuration(duration + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Maniacal Rage",
                "Berserk - Master Upgrade",
                "+20% Additional damage increase but increase\ndamage taken by 10%\n\nGain 0.2% Crit chance and Crit Multiplier for\neach instance of damage you deal to an enemy\nwhile Berserk is active. (Max 30%)",
                50000,
                () -> {
                    ability.setDamageIncrease(ability.getDamageIncrease() + 20);
                    ability.setDamageTakenIncrease(ability.getDamageTakenIncrease() + 10);
                    ability.setPveUpgrade(true);
                }
        );
    }
}

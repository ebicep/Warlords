package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilties.Repentance;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RepentanceBranch extends AbstractUpgradeBranch<Repentance> {

    float damageConvert = ability.getDamageConvertPercent();
    int duration = ability.getTickDuration();

    public RepentanceBranch(AbilityTree abilityTree, Repentance ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+0.5% Damage conversion",
                5000,
                () -> {
                    ability.setDamageConvertPercent(damageConvert + 0.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+1% Damage conversion",
                10000,
                () -> {
                    ability.setDamageConvertPercent(damageConvert + 1);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+1.5% Damage conversion",
                15000,
                () -> {
                    ability.setDamageConvertPercent(damageConvert + 1.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+2% Damage conversion",
                20000,
                () -> {
                    ability.setDamageConvertPercent(damageConvert + 2);
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
                "Revengeance",
                "Repentance - Master Upgrade",
                "Repentance's pool decay per second is reduced by 50% and the energy conversion based on damage taken is increased by 25%.",
                50000,
                () -> {
                    ability.setPoolDecay((int) (ability.getPoolDecay() * 0.5f));
                    ability.setEnergyConvertPercent(ability.getEnergyConvertPercent() * 1.25f);
                }
        );
    }
}
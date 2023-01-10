package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilties.Repentance;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RepentanceBranch extends AbstractUpgradeBranch<Repentance> {

    int damageConvert = ability.getDamageConvertPercent();
    int duration = ability.getDuration();

    public RepentanceBranch(AbilityTree abilityTree, Repentance ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+2% Damage conversion",
                5000,
                () -> {
                    ability.setDamageConvertPercent(damageConvert + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+3% Damage conversion",
                10000,
                () -> {
                    ability.setDamageConvertPercent(damageConvert + 3);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+4% Damage conversion",
                15000,
                () -> {
                    ability.setDamageConvertPercent(damageConvert + 4);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+5% Damage conversion",
                20000,
                () -> {
                    ability.setDamageConvertPercent(damageConvert + 5);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1s Duration",
                5000,
                () -> {
                    ability.setDuration(duration + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2s Duration",
                10000,
                () -> {
                    ability.setDuration(duration + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3s Duration",
                15000,
                () -> {
                    ability.setDuration(duration + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4s Duration",
                20000,
                () -> {
                    ability.setDuration(duration + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Revengeance",
                "Repentance - Master Upgrade",
                "Repentance's pool decay per second is reduced by 80% and the energy conversion based on damage taken is increased by 50%.",
                50000,
                () -> {
                    ability.setPoolDecay((int) (ability.getPoolDecay() * 0.2f));
                    ability.setEnergyConvertPercent(ability.getEnergyConvertPercent() * 1.5f);
                }
        );
    }
}

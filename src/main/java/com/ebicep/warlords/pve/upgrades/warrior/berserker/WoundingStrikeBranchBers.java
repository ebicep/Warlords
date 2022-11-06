package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WoundingStrikeBranchBers extends AbstractUpgradeBranch<WoundingStrikeBerserker> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();
    int woundDuration = ability.getWoundingDuration();

    public WoundingStrikeBranchBers(AbilityTree abilityTree, WoundingStrikeBerserker ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy cost\n+1s Wounding duration",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                    ability.setWoundingDuration(woundDuration + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy cost\n+2s Wounding duration",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                    ability.setWoundingDuration(woundDuration + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy cost\n+3s Wounding duration",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                    ability.setWoundingDuration(woundDuration + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy cost\n+4s Wounding duration",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setWoundingDuration(woundDuration + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Lacerating Strike",
                "Wounding Strike - Master Upgrade",
                "Wounding Strike now applies BLEED instead of wounding.\n\nBLEED: Enemies afflicted take 60% more damage from Wounding " +
                        "Strike while Blood Lust is active. Bleeding enemies have healing reduced by 80% and lose 0.5% of their max " +
                        "health per second.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );

    }
}

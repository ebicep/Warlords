package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.FallenSouls;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FallenSoulsBranch extends AbstractUpgradeBranch<FallenSouls> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();

    public FallenSoulsBranch(AbilityTree abilityTree, FallenSouls ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+15% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+30% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+45% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.45f);
                    ability.setMaxDamageHeal(maxDamage * 1.45f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+60% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.6f);
                    ability.setMaxDamageHeal(maxDamage * 1.6f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy cost",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy cost",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy cost",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy cost",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                }
        ));

        masterUpgrade = new Upgrade(
                "Soul Swarm",
                "Fallen Souls - Master Upgrade",
                "Fallen Souls shoots two additional projectiles.",
                50000,
                () -> {
                    ability.setShotsFiredAtATime(5);
                }
        );
    }
}

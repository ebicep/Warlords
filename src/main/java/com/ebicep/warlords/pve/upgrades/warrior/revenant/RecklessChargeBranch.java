package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.RecklessCharge;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RecklessChargeBranch extends AbstractUpgradeBranch<RecklessCharge> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();
    int stunDuration = ability.getStunTimeInTicks();

    public RecklessChargeBranch(AbilityTree abilityTree, RecklessCharge ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+10% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+20% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+30% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+40% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+0.25s Immobilize duration",
                5000,
                () -> {
                    ability.setStunTimeInTicks(stunDuration + 5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+0.5s Immobilize duration",
                10000,
                () -> {
                    ability.setStunTimeInTicks(stunDuration + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+0.75s Immobilize duration\n-15 Energy cost",
                15000,
                () -> {
                    ability.setStunTimeInTicks(stunDuration + 15);
                    ability.setEnergyCost(energyCost - 15);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+1s Immobilize duration\n-30 Energy cost",
                20000,
                () -> {
                    ability.setStunTimeInTicks(stunDuration + 20);
                    ability.setEnergyCost(energyCost - 30);
                }
        ));

        masterUpgrade = new Upgrade(
                "Reckless Rampage",
                "Reckless Charge - Master Upgrade",
                "+50% Additional damage\n\nReckless Charge stuns enemies for 3 seconds. Additionally, allies you charge through will receive 100% more healing for 8 seconds.",
                50000,
                () -> {
                    ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.5f);
                    ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.5f);

                    ability.setStunTimeInTicks(60);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Reverberation",
                "Reckless Charge - Master Upgrade",
                """
                        +75% Additional damage to CRIPPLED enemies.
                                                
                        Reduce the cooldown of Undying Army by 1s for each enemy killed with Reckless Charge. Additionally, allies you charge through receive 100% more healing for 8 seconds.
                        """,
                50000,
                () -> {
                }
        );
    }
}

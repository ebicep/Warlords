package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.RecklessCharge;
import com.ebicep.warlords.pve.upgrades.*;

public class RecklessChargeBranch extends AbstractUpgradeBranch<RecklessCharge> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    int stunDuration = ability.getStunTimeInTicks();

    public RecklessChargeBranch(AbilityTree abilityTree, RecklessCharge ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 10f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.NamedUpgradeType() {

                    @Override
                    public String getName() {
                        return "Disarm";
                    }

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "s Immobilize Duration";
                    }

                    @Override
                    public void run(float value) {
                        ability.setStunTimeInTicks((int) (stunDuration + value * 20));
                    }
                }, .25f)
                .addUpgradeEnergy(ability, 15f, 3, 4)
                .addTo(treeB);

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
                                                
                        Reduce the cooldown of Undying Army by 1s for each enemy killed with Reckless Charge, max 5s. Additionally, allies you charge through receive 100% more healing for 8 seconds.
                        """,
                50000,
                () -> {
                }
        );
    }
}

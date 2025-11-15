package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilities.Inferno;
import com.ebicep.warlords.pve.upgrades.*;

public class InfernoBranch extends AbstractUpgradeBranch<Inferno> {

    int critMultiplierIncrease = ability.getCritMultiplierIncrease();

    public InfernoBranch(AbilityTree abilityTree, Inferno ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Crit Multiplier";
                    }

                    @Override
                    public void run(float value) {
                        ability.setCritMultiplierIncrease((int) (critMultiplierIncrease + value));
                    }
                }, 20f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 40f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Dante’s Inferno",
                "Inferno - Master Upgrade",
                """
                        Increase duration by 5s. While inferno is active fireball costs 5 less energy.
                        
                        Additionally, damaging the same target will cause your damage against that target to increase by 5% (up to 50%) BURN now activates every 0.5s.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 100);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Promethean Gaze",
                "Inferno - Master Upgrade",
                """
                        While Inferno is active, increased damage by 20%, Ignition damage is doubled, and enemies killed by Ignition damage will refund the caster 30 energy.
                        """,
                50000,
                () -> {

                }
        );
    }

}

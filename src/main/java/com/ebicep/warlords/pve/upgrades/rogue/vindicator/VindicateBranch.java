package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilities.Vindicate;
import com.ebicep.warlords.pve.upgrades.*;

public class VindicateBranch extends AbstractUpgradeBranch<Vindicate> {
    float damageReduction = ability.getVindicateDamageReduction();

    public VindicateBranch(AbilityTree abilityTree, Vindicate ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.ShieldUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Damage Reduction";
                    }

                    @Override
                    public void run(float value) {
                        ability.setVindicateDamageReduction(damageReduction + value);
                    }
                }, 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgradeDuration(ability, 20f)
                .addUpgradeDuration(ability::setDamageReductionTickDuration, ability::getDamageReductionTickDuration, 20f, true, true)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Reflective Barrier",
                "Vindicate - Master Upgrade",
                "Enemies who try to attack you while Vindicate is active are pushed back and reflect 90% of the damage you take back.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Liberation",
                "Vindicate - Master Upgrade",
                """
                        When activated, allies within the radius are granted 15% damage reduction and enemies within the radius are silenced for 10s.
                        """,
                50000,
                () -> {

                }
        );
    }
}

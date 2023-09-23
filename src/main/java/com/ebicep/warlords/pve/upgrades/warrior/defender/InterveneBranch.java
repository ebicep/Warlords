package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.Intervene;
import com.ebicep.warlords.pve.upgrades.*;

import javax.annotation.Nonnull;

public class InterveneBranch extends AbstractUpgradeBranch<Intervene> {

    float maxDamagePrevented = ability.getMaxDamagePrevented();

    public InterveneBranch(AbilityTree abilityTree, Intervene ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.ShieldUpgradeType() {
                    @Nonnull
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Max Damage Prevented";
                    }

                    @Override
                    public void run(float value) {
                        ability.setMaxDamagePrevented(maxDamagePrevented + value);
                    }
                }, 250f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Intersection",
                "Intervene - Master Upgrade",
                "Remove the damage, cast and break range limit on Intervene. Additionally, reduce damage taken by 30%",
                50000,
                () -> {
                    ability.setDamageReduction(ability.getDamageReduction() - 30);
                    ability.setMaxDamagePrevented(10000000);
                    ability.setRadius(300);
                    ability.setBreakRadius(300);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Interference",
                "Intervene - Master Upgrade",
                """
                        Remove the damage, cast and break range limit on Intervene. Additionally, Intervene gives a 20% speed increase to the caster and person intervened.
                        """,
                50000,
                () -> {
                    ability.setMaxDamagePrevented(10000000);
                    ability.setRadius(300);
                    ability.setBreakRadius(300);
                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.Sanctuary;
import com.ebicep.warlords.pve.upgrades.*;

public class SanctuaryBranch extends AbstractUpgradeBranch<Sanctuary> {

    public SanctuaryBranch(AbilityTree abilityTree, Sanctuary ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.DurationUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "s Hex Duration";
                    }

                    @Override
                    public void run(float value) {
                        ability.setHexTickDurationIncrease(ability.getHexTickDurationIncrease() + (int) value);
                    }
                }, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Oasis",
                "Sanctuary - Master Upgrade",
                """
                        All allies with max stacks of Fortifying Hex gain an additional 15% damage reduction. Reflection damage ignores enemy defenses and resistances.
                        """,
                50000,
                () -> {
                    ability.setAdditionalDamageReduction(ability.getAdditionalDamageReduction() + 15);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Asylum",
                "Sanctuary - Master Upgrade",
                """
                        For the duration of Sanctuary, reduce the cooldown of Guardian Beam by 33% while Sanctuary is active and increase the shield provided to allies by 15%.
                        """,
                50000,
                () -> {
                }
        );
    }

}

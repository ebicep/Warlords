package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.Sanctuary;
import com.ebicep.warlords.pve.upgrades.*;

public class SanctuaryBranch extends AbstractUpgradeBranch<Sanctuary> {

    public SanctuaryBranch(AbilityTree abilityTree, Sanctuary ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
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
                        +5s Duration.
                        
                        All allies with max stacks of Fortifying Hex gain an additional 15% damage reduction. Allies revived by Sanctuary deal true damage equal to 2x of the player's total health to all enemies within a 15 block radius.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 100);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Asylum",
                "Sanctuary - Master Upgrade",
                """
                        For the duration of Sanctuary, reduce the cooldown of Guardian Beam by 30% while Sanctuary is active and increase the shield provided to allies by 600.
                        """,
                50000,
                () -> {
                }
        );
    }

}

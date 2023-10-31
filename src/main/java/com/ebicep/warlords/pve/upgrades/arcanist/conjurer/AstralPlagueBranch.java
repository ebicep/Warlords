package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.AstralPlague;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class AstralPlagueBranch extends AbstractUpgradeBranch<AstralPlague> {

    public AstralPlagueBranch(AbilityTree abilityTree, AstralPlague ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 30f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Virulent State",
                "Astral Plague - Master Upgrade",
                """
                        For the duration of Astral Plague, increase Crit Multiplier by 40%, and Soulfire Beam is guaranteed to crit on enemies with max Hex stacks.
                        """,
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Crimson Catastrophe",
                "Astral Plague - Master Upgrade",
                """
                        For the duration of Astral Plague, reduce the cooldown of Soulfire Beam by 40% and increase the damage of Soulfire Beam by 40%.
                        """,
                50000,
                () -> {
                }
        );
    }

}

package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.DivineBlessing;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class DivineBlessingBranch extends AbstractUpgradeBranch<DivineBlessing> {

    public DivineBlessingBranch(AbilityTree abilityTree, DivineBlessing ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Graceful Ascent",
                "Divine Blessing - Master Upgrade",
                """
                        Lethal damage healing increased to 30%. All allies restore another 800 health after Divine Blessing ends.
                        """,
                50000,
                () -> {
                    ability.setLethalDamageHealing(ability.getLethalDamageHealing() + 30);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Dampened Descent",
                "Divine Blessing - Master Upgrade",
                """
                        Upon cast, all allies within 10 blocks will have their current Merciful Hex duration reset. These reset stacks of Merciful Hex will be boosted by Divine Blessing. Additionally, reduce the cooldown of Ray of Light by 33% while Divine Blessing is active.
                        """,
                50000,
                () -> {
                }
        );
    }

}

package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class RayOfLightBranch extends AbstractUpgradeBranch<RayOfLight> {

    @Override
    public void runOnce() {
        Value.RangedValueCritable healing = ability.getHealValues().getRayHealing();
        healing.min().addMultiplicativeModifierAdd("PvE", .3f);
        healing.max().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public RayOfLightBranch(AbilityTree abilityTree, RayOfLight ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability.getHealValues().getRayHealing(), 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Divine Light",
                "Ray of Light - Master Upgrade",
                """
                        Ray of Light will grant allies a 15% damage bonus, 35% if the ally has max stacks of Merciful Hex, for 5s.
                        """,
                50000,
                () -> {
                }
        );
        masterUpgrade2 = new Upgrade(
                "Volatile Rays",
                "Ray of Light - Master Upgrade",
                """
                        Ray of Light fires two additional beams.
                        """,
                50000,
                () -> {
                    ability.setShotsFiredAtATime(3);
                }
        );
    }

}

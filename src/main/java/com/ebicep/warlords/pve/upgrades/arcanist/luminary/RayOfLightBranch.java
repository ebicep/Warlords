package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class RayOfLightBranch extends AbstractUpgradeBranch<RayOfLight> {

    public RayOfLightBranch(AbilityTree abilityTree, RayOfLight ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgradeHitBox(ability, 2f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability.getHealValues().getRayHealing(), 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Divine Light",
                "Ray of Light - Master Upgrade",
                """
                        Ray of Light will grant allies a 5% damage bonus, 20% if the ally has max stacks of Merciful Hex, for 5 seconds.
                        """,
                50000,
                () -> {
                }
        );
        masterUpgrade2 = new Upgrade(
                "Volatile Rays",
                "Ray of Light - Master Upgrade",
                """
                        +45% Crit Multiplier
                        
                        Ray of Light fires two additional beams.
                        """,
                50000,
                () -> {
                    ability.getHealValues().getRayHealing().critMultiplier().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 45);
                    ability.setShotsFiredAtATime(3);
                }
        );
    }

    @Override
    public void runOnce() {
        Value.RangedValueCritable healing = ability.getHealValues().getRayHealing();
        healing.min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
        healing.max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
    }

}

package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.FallenSouls;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class FallenSoulsBranch extends AbstractUpgradeBranch<FallenSouls> {

    @Override
    public void runOnce() {
        abilityTree.getWarlordsPlayer().getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "PvE (Base)", 300);
    }

    public FallenSoulsBranch(AbilityTree abilityTree, FallenSouls ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getFallenSoulDamage(), 15f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Grave Compass",
                "Fallen Souls - Master Upgrade",
                """
                        Fallen Souls bends toward enemies targeting your allies within a 20 block radius.
        
                        Enemies hit by Fallen Souls are forced to target you.
                        """,
                50000,
                () -> {
                }
        );

        masterUpgrade2 = new Upgrade(
                "Soul Feast",
                "Fallen Souls - Master Upgrade",
                """
                        Enemies struck by Fallen Souls will have their damage dealt permanently reduced by 2.5%, stacking up to 35%.
                        """,
                50000,
                () -> {
                }
        );
    }

    public static class SoulFeast {
        private float damageMultiplier = 0.975f;

        public float getDamageMultiplier() {
            return damageMultiplier;
        }

        public void reduce() {
            damageMultiplier = Math.max(0.65f, damageMultiplier - 0.025f);
        }
    }
}

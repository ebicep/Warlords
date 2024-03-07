package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.FallenSouls;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class FallenSoulsBranch extends AbstractUpgradeBranch<FallenSouls> {

    public FallenSoulsBranch(AbilityTree abilityTree, FallenSouls ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 15f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Soul Swarm",
                "Fallen Souls - Master Upgrade",
                "Fallen Souls shoots two additional projectiles.",
                50000,
                () -> {
                    ability.setShotsFiredAtATime(5);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Soul Feast",
                "Fallen Souls - Master Upgrade",
                """
                        Enemies struck by Fallen Souls will have their damage dealt permanently reduced by 1.5%, stacking up to 15%.
                        """,
                50000,
                () -> {
                }
        );
    }

    public static class SoulFeast {
        private float damageMultiplier = 0.985f;

        public float getDamageMultiplier() {
            return damageMultiplier;
        }

        public void reduce() {
            damageMultiplier = Math.max(0.85f, damageMultiplier - 0.015f);
        }
    }
}

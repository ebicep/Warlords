package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.FallenSouls;
import com.ebicep.warlords.pve.upgrades.*;

public class FallenSoulsBranch extends AbstractUpgradeBranch<FallenSouls> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public FallenSoulsBranch(AbilityTree abilityTree, FallenSouls ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 15f)
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

package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.pve.upgrades.*;

public class RayOfLightBranch extends AbstractUpgradeBranch<RayOfLight> {

    float minDamageHeal;
    float maxDamageHeal;

    @Override
    public void runOnce() {
        ability.multiplyMinMax(1.3f);
    }

    public RayOfLightBranch(AbilityTree abilityTree, RayOfLight ability) {
        super(abilityTree, ability);

        minDamageHeal = ability.getMinDamageHeal();
        maxDamageHeal = ability.getMaxDamageHeal();

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamageHeal * v);
                        ability.setMaxDamageHeal(maxDamageHeal * v);
                    }
                }, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Divine Light",
                "Ray of Light - Master Upgrade",
                """
                        Ray of Light will grant allies a 15% damage bonus, 50% if the ally has max stacks of Merciful Hex, for 5s.
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

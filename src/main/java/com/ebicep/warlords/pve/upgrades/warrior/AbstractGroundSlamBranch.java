package com.ebicep.warlords.pve.upgrades.warrior;

import com.ebicep.warlords.abilities.internal.AbstractGroundSlam;
import com.ebicep.warlords.pve.upgrades.*;

public class AbstractGroundSlamBranch<T extends AbstractGroundSlam> extends AbstractUpgradeBranch<T> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public AbstractGroundSlamBranch(AbilityTree abilityTree, T ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 5f)
                .addTo(treeA);


        UpgradeTreeBuilder
                .create()
                .addUpgradeHitBox(ability, 1f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Earthen Tremor",
                "Ground Slam - Master Upgrade",
                "Casting Ground Slam will leap you in the air for a short duration. Upon landing, activate a second Ground Slam for 150% of the original damage.",
                50000,
                () -> {

                }
        );
    }
}

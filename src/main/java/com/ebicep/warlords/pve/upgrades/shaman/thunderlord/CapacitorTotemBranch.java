package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.CapacitorTotem;
import com.ebicep.warlords.pve.upgrades.*;

public class CapacitorTotemBranch extends AbstractUpgradeBranch<CapacitorTotem> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public CapacitorTotemBranch(AbilityTree abilityTree, CapacitorTotem ability) {
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
                }, 12.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Incapacitating Totem",
                "Capacitor Totem - Master Upgrade",
                "Each Capacitor Totem proc increases the hit radius by 0.5 blocks and all enemies hit have their damage resistance permanently reduced by 20%",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Electric Artifact",
                "Capacitor Totem - Master Upgrade",
                """
                        Each Capacitor Totem proc increases the duration of the totem by 0.5s with a cap of 10s and for every enemy hit the caster gains 1% damage reduction for the duration of the totem with a cap of 15%.
                        """,
                50000,
                () -> {

                }
        );
    }

}

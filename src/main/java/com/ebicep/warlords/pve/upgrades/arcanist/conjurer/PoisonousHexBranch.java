package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.PoisonousHex;
import com.ebicep.warlords.pve.upgrades.*;

public class PoisonousHexBranch extends AbstractUpgradeBranch<PoisonousHex> {

    float minDamage;
    float maxDamage;

    public PoisonousHexBranch(AbilityTree abilityTree, PoisonousHex ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.multiplyMinMax(1.3f);
            ability.setMaxEnemiesHit(4);
        }
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgradeEnergy(ability)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+50% Projectile Speed";
                    }

                    @Override
                    public void run(float value) {
                        ability.setProjectileSpeed(ability.getProjectileSpeed() * 1.5);
                    }
                }, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Intrusive Hex",
                "Poisonous Hex - Master Upgrade",
                """
                        Poisonous Hex now pierces through all enemies.
                        """,
                50000,
                () -> {
                    ability.setMaxEnemiesHit(200);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Baneful Hex",
                "Poisonous Hex - Master Upgrade",
                """
                        +20% Damage
                                                
                        Poisonous Hex now pierces through 4 enemies.
                        """,
                50000,
                () -> {
                    ability.multiplyMinMax(1.2f);
                    ability.setMaxEnemiesHit(4);
                }
        );
    }

}

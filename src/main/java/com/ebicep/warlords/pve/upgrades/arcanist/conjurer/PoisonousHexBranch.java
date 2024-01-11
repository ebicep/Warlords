package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.PoisonousHex;
import com.ebicep.warlords.pve.upgrades.*;

public class PoisonousHexBranch extends AbstractUpgradeBranch<PoisonousHex> {

    float minDamage;
    float maxDamage;

    @Override
    public void runOnce() {
        ability.multiplyMinMax(1.3f);
        ability.setMaxEnemiesHit(4);
    }

    public PoisonousHexBranch(AbilityTree abilityTree, PoisonousHex ability) {
        super(abilityTree, ability);

        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();

        UpgradeTreeBuilder
                .create(abilityTree, this)
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
                .create(abilityTree, this)
                .addUpgradeEnergy(ability)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Projectile Speed";
                    }

                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setProjectileSpeed(ability.getProjectileSpeed() * value);
                    }
                }, 50f, 4)
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
                        +35% Damage
                                                
                        Poisonous Hex damage occurs every 1s instead of 2s.
                        """,
                50000,
                () -> {
                    ability.multiplyMinMax(1.35f);
                    ability.setTicksBetweenDot(20);
                }
        );
    }

}

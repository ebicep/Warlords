package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.pve.upgrades.*;

public class FortifyingHexBranch extends AbstractUpgradeBranch<FortifyingHex> {

    float minDamage;
    float maxDamage;

    @Override
    public void runOnce() {
        ability.multiplyMinMax(1.3f);
        ability.setMaxEnemiesHit(2);
        ability.setMaxAlliesHit(3);
    }

    public FortifyingHexBranch(AbilityTree abilityTree, FortifyingHex ability) {
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
                }, 5f)
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
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeHitBox(ability, .5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Bolstering Hex",
                "Fortifying Hex - Master Upgrade",
                """
                        -15 Additional energy cost.
                                                
                        Fortifying Hex can now pierce through infinite targets. Additionally, increase the damage reduction of Fortifying Hex by 3%.
                        """,
                50000,
                () -> {
                    ability.setMaxEnemiesHit(200);
                    ability.setMaxAlliesHit(200);
                    ability.getEnergyCost().addAdditiveModifier("Bolstering Hex", -15);
                    ability.getDamageReduction().addAdditiveModifier("Bolstering Hex", 3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Augmenting Hex",
                "Fortifying Hex - Master Upgrade",
                """
                        Fortifying Hex now explodes on contact, targets hit receive a Weakening Hex stack. Each stack of Weakening Hex on enemies increases damage taken by 5%.
                        """,
                50000,
                () -> {
                }
        );
    }

}

package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.PoisonousHex;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;

public class PoisonousHexBranch extends AbstractUpgradeBranch<PoisonousHex> {

    @Override
    public void runOnce() {
        Value.RangedValueCritable hexDamage = ability.getDamageValues().getHexDamage();
        hexDamage.min().addMultiplicativeModifierAdd("PvE", .3f);
        hexDamage.max().addMultiplicativeModifierAdd("PvE", .3f);
        ability.setMaxEnemiesHit(4);
    }

    public PoisonousHexBranch(AbilityTree abilityTree, PoisonousHex ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getHexDamage(), 7.5f)
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
                    Value.RangedValueCritable damage = ability.getDamageValues().getHexDamage();
                    damage.min().addMultiplicativeModifierAdd("Master Upgrade Branch", .35f);
                    damage.max().addMultiplicativeModifierAdd("Master Upgrade Branch", .35f);
                    ability.setTicksBetweenDot(20);
                }
        );
    }

}

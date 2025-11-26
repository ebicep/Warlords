package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.PoisonousHex;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class PoisonousHexBranch extends AbstractUpgradeBranch<PoisonousHex> {

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
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value / 100);
                    }
                            }, ability.getProjectileSpeed().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Upgrade Branch", 0), 50f, 4
                )
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Intrusive Hex",
                "Poisonous Hex - Master Upgrade",
                """
                        +28 Pierce
                        
                        Poisonous Hex damage occurs every 0.5s instead of 2s.
                        """,
                50000,
                () -> {
                    ability.setTicksBetweenDot(10);
                    ability.setMaxEnemiesHit(ability.getMaxEnemiesHit() + 28);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Baneful Hex",
                "Poisonous Hex - Master Upgrade",
                """
                        +50% Damage
                        +12 Pierce
                        
                        PHEX can now stack up to 5 times.
                        """,
                50000,
                () -> {
                    Value.RangedValueCritable damage = ability.getDamageValues().getHexDamage();
                    damage.min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Master Upgrade Branch", .50f);
                    damage.max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Master Upgrade Branch", .50f);
                    ability.setMaxEnemiesHit(ability.getMaxEnemiesHit() + 12);
                    ability.setMaxStacks(5);
                }
        );
    }

    @Override
    public void runOnce() {
        Value.RangedValueCritable hexDamage = ability.getDamageValues().getHexDamage();
        hexDamage.min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
        hexDamage.max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
        ability.setMaxEnemiesHit(4);
    }

}

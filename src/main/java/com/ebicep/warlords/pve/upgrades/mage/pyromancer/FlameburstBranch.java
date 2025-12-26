package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class FlameburstBranch extends AbstractUpgradeBranch<FlameBurst> {

    public FlameburstBranch(AbilityTree abilityTree, FlameBurst ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getFlameBurstDamage(), 7.5f)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeSplash(ability, .5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Crit Multiplier";
                    }

                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value);
                    }
                    }, ability.getDamageValues().getFlameBurstDamage().critMultiplier().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Upgrade Branch", 0), 15f, 4
                )
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Flame Awakening",
                "Flame Burst - Master Upgrade",
                "Flame Burst deals significantly more damage and ramps up Crit Chance, Crit Multiplier and damage very quickly per blocks traveled at the cost " +
                        "of heavily reduced projectile speed.",
                50000,
                () -> {
                    ability.setProjectileWidth(0.72D);
                    ability.getProjectileSpeed().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Master Upgrade Branch", .2f);
                    Value.RangedValueCritable damage = ability.getDamageValues().getFlameBurstDamage();
                    damage.min().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Master Upgrade Branch", 1f);
                    damage.max().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Master Upgrade Branch", 1f);
                    ability.getSplashRadius().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 5);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Backfire",
                "Flame Burst - Master Upgrade",
                """
                        Converts the burst into a flaming boomerang that can pierce multiple targets dealing 5% more damage per target hit (Max 75%).
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().setBaseValue(ability.getSplashRadius().getCalculatedValue()/2.5f);
                    ability.getMaxDistance().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -132);
                    ability.getSplashRadius().setBaseValue(0);
                }
        );
    }
}

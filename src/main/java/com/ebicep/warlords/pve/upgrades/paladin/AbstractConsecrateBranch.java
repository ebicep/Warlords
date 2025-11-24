package com.ebicep.warlords.pve.upgrades.paladin;

import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public abstract class AbstractConsecrateBranch<T extends AbstractConsecrate> extends AbstractUpgradeBranch<T> {

    public AbstractConsecrateBranch(AbilityTree abilityTree, T ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getConsecrateDamage(), 10f)
                .addUpgradeCooldown(ability, 0.15f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, 0.25f)
                .addUpgradeDuration(ability, 20f, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Sanctify",
                "Consecrate - Master Upgrade",
                """
                        +150% Damage
                        -30 Energy cost
                        +2 Additional blocks hit radius
                        +20% Cooldown reduction""",
                50000,
                () -> {
                    Value.RangedValueCritable damage = ability.getConsecrateDamage();
                    damage.min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Master Upgrade Branch", 1.50f);
                    damage.max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Master Upgrade Branch", 1.50f);
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -30);
                    ability.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 2);
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Sanctify", 0.8f);
                }
        );
    }
}

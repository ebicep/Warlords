package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.IncendiaryCurse;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class IncendiaryCurseBranch extends AbstractUpgradeBranch<IncendiaryCurse> {

    public IncendiaryCurseBranch(AbilityTree abilityTree, IncendiaryCurse ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getCurseDamage(), 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgradeHitBox(ability, .5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Blazing Curse",
                "Incendiary Curse - Master Upgrade",
                "All enemies hit become disoriented. Increase the damage they take by 30% for 5 seconds.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Unforeseen Curse",
                "Incendiary Curse - Master Upgrade",
                """
                        +30% Damage
                        
                        Increase the block radius by 3. Additionally, every enemy stunned gives 10 energy (Max 200).
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade", 3);

                    Value.RangedValueCritable damage = ability.getDamageValues().getCurseDamage();
                    damage.min().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .3f);
                    damage.max().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .3f);
                }
        );
    }

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getCurseDamage();
        ability.getEnergyCost().setBaseValue(40);
        ability.setBlindDurationInTicks(30);
        damage.min().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .3f);
        damage.max().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .3f);
    }
}

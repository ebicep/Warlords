package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.MercifulHex;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class MercifulHexBranch extends AbstractUpgradeBranch<MercifulHex> {

    public MercifulHexBranch(AbilityTree abilityTree, MercifulHex ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability.getHealValues(), 15f)
                .addUpgradeHitBox(ability, 1f, 3, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeDamage(ability.getDamageValues().getHexDamage(), 15f, 3, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Benevolent Hex",
                "Merciful Hex - Master Upgrade",
                """
                        +100% Projectile speed
                        -5 Energy cost
                        +100 Healing (excluding HoT)
                        
                        All allies hit receive 1 extra stack of Merciful Hex.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -5);
                    ability.getProjectileSpeed().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Master Upgrade Branch", 2);
                    ability.setHexStacksPerHit(ability.getHexStacksPerHit() + 1);
                    ability.setHexStacksPerHitAfter(ability.getHexStacksPerHitAfter() + 1);
                    Value.RangedValueCritable healing = ability.getHealValues().getHexHealing();
                    healing.min().addModifier(FloatModifiable.ModifierType.ADDITIVE, "PvE", 100f);
                    healing.max().addModifier(FloatModifiable.ModifierType.ADDITIVE, "PvE", 100f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Rainbow Hex",
                "Merciful Hex - Master Upgrade",
                """
                        -15 Energy cost
                        
                        Merciful Hex healing occurs every .5s instead of 2s.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -15);
                    ability.setTicksBetweenDot(10);
                }
        );
    }

    @Override
    public void runOnce() {
        ability.getDamageValues()
               .getValues()
               .forEach(value -> {
                   value.forEachValue(floatModifiable -> floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .15f));
               });
        ability.getHealValues()
               .getValues()
               .forEach(value -> {
                   value.forEachValue(floatModifiable -> floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .15f));
               });
        ability.setMaxAlliesHit(3);
    }

}

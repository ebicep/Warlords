package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class FortifyingHexBranch extends AbstractUpgradeBranch<FortifyingHex> {

    public FortifyingHexBranch(AbilityTree abilityTree, FortifyingHex ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getHexDamage(), 5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Projectile Speed";
                    }

                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value / 100);
                    }
                            }, ability.getProjectileSpeed().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Upgrade Branch", 0), 50f, 4
                )
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
                        -10 Energy cost.
                       
                        Fortifying Hex can now pierce through infinite targets. Additionally, increase the damage reduction of Fortifying Hex by 3%.
                        """,
                50000,
                () -> {
                    ability.setMaxEnemiesHit(200);
                    ability.setMaxAlliesHit(200);
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Bolstering Hex", -10);
                    ability.getDamageReduction().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Bolstering Hex", 3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Augmenting Hex",
                "Fortifying Hex - Master Upgrade",
                """
                        Fortifying Hex now explodes on contact, targets hit receive a Weakening Hex stack. Each stack of Weakening Hex on enemies increases damage taken by 5% (max 4 stacks).
                        """,
                50000,
                () -> {

                }
        );
    }

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getHexDamage();
        damage.min().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .15f);
        damage.max().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .15f);
        ability.setMaxEnemiesHit(2);
        ability.setMaxAlliesHit(3);
    }

}

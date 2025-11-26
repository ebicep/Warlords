package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class CripplingStrikeBranch extends AbstractUpgradeBranch<CripplingStrike> {


    public CripplingStrikeBranch(AbilityTree abilityTree, CripplingStrike ability) {
        super(abilityTree, ability);
        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Crippling Slash",
                "Crippling Strike - Master Upgrade",
                """
                        -10 Energy cost
                        
                        Crippling Strike deals damage to 4 additional targets, the cripple status now reduces enemy damage dealt by 40%
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -10);
                    ability.setCripple(40);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Productive Strike",
                "Crippling Strike - Master Upgrade",
                """
                        -20 Energy cost
                        
                        Crippling Strikes deals damage to 3 additional targets, Strike kills will reduce the cooldown of Orbs of Life by 0.5s.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -20);
                }
        );
    }

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
        damage.min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
        damage.max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
    }
}
